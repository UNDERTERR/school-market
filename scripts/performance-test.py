import http.client
import json
import time
import os

# 性能测试配置
TARGET_URL = os.environ.get('TARGET_URL', 'http://localhost:8080')
API_ENDPOINTS = [
    '/',
    '/api/products',
    '/api/categories',
    '/api/cart'
]

# 测试参数
VUS = 50  # 虚拟用户数
DURATION = '2m'  # 测试持续时间
RAMP_UP = '30s'  # 启动时间

# HTTP请求配置
http_req_timeout = '10s'
http_req_connect_timeout = '5s'

# 基准测试函数
def setup():
    print(f"Starting performance test against {TARGET_URL}")
    print(f"Virtual users: {VUS}")
    print(f"Duration: {DURATION}")
    print(f"Endpoints to test: {API_ENDPOINTS}")

def teardown():
    print("Performance test completed")

# 测试场景
def test_api_endpoints():
    """测试主要API端点的性能"""
    results = {}
    
    for endpoint in API_ENDPOINTS:
        url = f"{TARGET_URL}{endpoint}"
        print(f"Testing endpoint: {endpoint}")
        
        # 简单的GET请求测试
        try:
            start_time = time.time()
            response = http_client.get(url, timeout=http_req_timeout)
            end_time = time.time()
            
            results[endpoint] = {
                'status_code': response.status_code,
                'response_time': end_time - start_time,
                'success': 200 <= response.status_code < 300
            }
            
            print(f"  Status: {response.status_code}, Time: {results[endpoint]['response_time']:.2f}s")
            
        except Exception as e:
            results[endpoint] = {
                'status_code': 0,
                'response_time': 0,
                'success': False,
                'error': str(e)
            }
            print(f"  Error: {e}")
    
    return results

# 负载测试
def load_test():
    """模拟负载测试"""
    print("Starting load test...")
    
    # 模拟多个并发请求
    import threading
    import queue
    
    results_queue = queue.Queue()
    
    def worker():
        results = test_api_endpoints()
        results_queue.put(results)
    
    threads = []
    for i in range(VUS):
        thread = threading.Thread(target=worker)
        threads.append(thread)
        thread.start()
    
    # 等待所有线程完成
    for thread in threads:
        thread.join()
    
    # 收集结果
    all_results = []
    while not results_queue.empty():
        all_results.append(results_queue.get())
    
    return all_results

# 生成报告
def generate_report(results):
    """生成性能测试报告"""
    report = {
        'target_url': TARGET_URL,
        'virtual_users': VUS,
        'duration': DURATION,
        'timestamp': time.time(),
        'results': results
    }
    
    # 计算统计信息
    if results:
        # 计算平均响应时间
        total_time = 0
        successful_requests = 0
        total_requests = 0
        
        for result in results:
            for endpoint, data in result.items():
                total_requests += 1
                if data.get('success', False):
                    successful_requests += 1
                    total_time += data.get('response_time', 0)
        
        if successful_requests > 0:
            avg_response_time = total_time / successful_requests
            success_rate = (successful_requests / total_requests) * 100
        else:
            avg_response_time = 0
            success_rate = 0
        
        report['statistics'] = {
            'total_requests': total_requests,
            'successful_requests': successful_requests,
            'success_rate_percent': success_rate,
            'average_response_time': avg_response_time
        }
    
    # 保存JSON报告（GitLab CI格式）
    with open('performance-report.json', 'w') as f:
        json.dump(report, f, indent=2)
    
    print("Performance report generated: performance-report.json")
    print(f"Success rate: {success_rate:.2f}%")
    print(f"Average response time: {avg_response_time:.2f}s")
    
    return report

# 主函数
if __name__ == "__main__":
    setup()
    
    try:
        # 运行负载测试
        results = load_test()
        
        # 生成报告
        generate_report(results)
        
        print("Performance test completed successfully")
        
    except Exception as e:
        print(f"Performance test failed: {e}")
        exit(1)
    
    finally:
        teardown()