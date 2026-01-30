import http.requests

// 基本配置
def target = args[0]
def contextName = "school-market-context"
def includePattern = "http://localhost:8080/.*"
def excludePattern = "http://localhost:8080/actuator/.*"

println "Starting ZAP Baseline Scan for: ${target}"

// 创建上下文
sendAndReceive( 'http://zap/JSON/core/view/newContext/', 'contextName=' + URLEncoder.encode(contextName, 'UTF-8') )

// 设置包含模式
sendAndReceive( 'http://zap/JSON/core/view/includeInContext/', 'contextName=' + URLEncoder.encode(contextName, 'UTF-8') + '&regex=' + URLEncoder.encode(includePattern, 'UTF-8') )

// 设置排除模式
sendAndReceive( 'http://zap/JSON/core/view/excludeFromContext/', 'contextName=' + URLEncoder.encode(contextName, 'UTF-8') + '&regex=' + URLEncoder.encode(excludePattern, 'UTF-8') )

// 启用蜘蛛扫描
println "Starting spider scan..."
spiderId = sendAndReceive( 'http://zap/JSON/spider/action/scan/', 'url=' + URLEncoder.encode(target, 'UTF-8') + '&maxChildren=10&recurse=true&contextName=' + URLEncoder.encode(contextName, 'UTF-8') ).spiderId

// 等待蜘蛛扫描完成
while (true) {
    progress = sendAndReceive( 'http://zap/JSON/spider/view/status/', 'scanId=' + spiderId ).status
    println "Spider progress: ${progress}%"
    if (progress == "100") break
    sleep(1000)
}

// 启动主动扫描
println "Starting active scan..."
scanId = sendAndReceive( 'http://zap/JSON/ascan/action/scan/', 'url=' + URLEncoder.encode(target, 'UTF-8') + '&recurse=true&inScopeOnly=true&scanPolicyName=Default Policy' ).scanId

// 等待主动扫描完成
while (true) {
    progress = sendAndReceive( 'http://zap/JSON/ascan/view/status/', 'scanId=' + scanId ).status
    println "Active scan progress: ${progress}%"
    if (progress == "100") break
    sleep(5000)
}

// 生成报告
println "Generating reports..."
htmlReport = sendAndReceive( 'http://zap/JSON/core/view/htmlreport/' )
mdReport = sendAndReceive( 'http://zap/JSON/core/view/mdreport/' )

// 保存报告
new File("zap-report.html").text = htmlReport
new File("zap-report.md").text = mdReport

// 获取告警数量
alerts = sendAndReceive( 'http://zap/JSON/core/view/alerts/', 'baseurl=' + URLEncoder.encode(target, 'UTF-8') ).alerts

println "Scan completed!"
println "High risk alerts: ${alerts.count { it.risk == 'High' }}"
println "Medium risk alerts: ${alerts.count { it.risk == 'Medium' }}"
println "Low risk alerts: ${alerts.count { it.risk == 'Low' }}"
println "Informational alerts: ${alerts.count { it.risk == 'Informational' }}"

// 生成GitLab CI报告
def gitlabReport = [
    version: "2.0",
    vulnerabilities: alerts.collect { alert ->
        [
            id: alert.id.toString(),
            category: "dast",
            name: alert.alert,
            description: alert.desc,
            cve: "",
            location: [
                dependency: [
                    package: [
                        name: alert.param ?: alert.url
                    ]
                ]
            ],
            identifiers: [
                [
                    type: "zap",
                    name: "ZAP-${alert.alert}",
                    value: alert.id.toString()
                ]
            ],
            links: [
                [
                    url: alert.reference ?: "",
                    name: "ZAP Reference"
                ]
            ]
        ]
    }
]

new File("gl-dast-report.json").text = new groovy.json.JsonBuilder(gitlabReport).toPrettyString()

println "GitLab DAST report generated: gl-dast-report.json"