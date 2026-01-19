📦 Product模块实体类详解
🏷️ 1. 基础管理类
BrandEntity - 品牌实体
@TableName("pms_brand")
作用: 管理商品品牌信息
- brandId: 品牌唯一标识
- name: 品牌名称
- logo: 品牌Logo地址
- descript: 品牌介绍
- showStatus: 显示状态(0-不显示,1-显示)
- firstLetter: 检索首字母(A-Z)
- sort: 排序权重
业务场景: 苹果、华为、小米等品牌管理
---
CategoryEntity - 商品分类实体
@TableName("pms_category")
作用: 管理商品三级分类体系
- catId: 分类ID
- name: 分类名称
- parentCid: 父分类ID
- catLevel: 层级(1-一级,2-二级,3-三级)
- showStatus: 显示状态
- sort: 排序
- children: 子分类(非数据库字段)
业务场景: 家电→手机→智能手机这样的树形分类
---
🏗️ 2. 属性体系类
AttrEntity - 商品属性实体
@TableName("pms_attr")
作用: 管理商品的具体属性
- attrId: 属性ID
- attrName: 属性名称(如"屏幕尺寸")
- searchType: 是否可搜索(0-否,1-是)
- attrType: 属性类型(0-销售,1-基本,2-混合)
- catelogId: 所属分类
- valueSelect: 可选值列表
业务场景: 屏幕尺寸、颜色、内存容量等商品属性
---
AttrGroupEntity - 属性分组实体
@TableName("pms_attr_group")
作用: 将属性按功能分组管理
- attrGroupId: 分组ID
- attrGroupName: 分组名称(如"基本参数","功能特性")
- catelogId: 所属分类
- descript: 分组描述
- sort: 排序
业务场景: 手机的"基本参数"、"功能特性"、"包装信息"分组
---
AttrAttrgroupRelationEntity - 属性分组关联实体
@TableName("pms_attr_attrgroup_relation")
作用: 多对多关系：属性与分组的关联
- attrId: 属性ID
- attrGroupId: 分组ID
- attrSort: 在分组内的排序
业务场景: 将"屏幕尺寸"属性归入"基本参数"分组
---
📱 3. 商品核心类
SpuInfoEntity - SPU实体
@TableName("pms_spu_info")
作用: 标准化产品单元管理
- id: SPU ID
- spuName: 商品名称(如"iPhone 14 Pro")
- spuDescription: 商品描述
- catalogId: 所属分类
- brandId: 所属品牌
- publishStatus: 上架状态(0-下架,1-上架)
- weight: 重量
业务场景: iPhone 14 Pro作为一个SPU，包含不同颜色和存储版本
---
SkuInfoEntity - SKU实体
@TableName("pms_sku_info")
作用: 具体的可售卖商品
- skuId: SKU ID
- spuId: 所属SPU
- skuName: SKU名称
- price: 价格
- saleCount: 销量
- skuDefaultImg: 默认图片
- skuTitle: 标题
- brandId: 品牌ID
- catalogId: 分类ID
业务场景: iPhone 14 Pro 128GB 深空黑色 (具体的可售卖商品)
---
🖼️ 4. 媒体资源类
SpuImagesEntity - SPU图片实体
@TableName("pms_spu_images")
作用: 管理SPU的展示图片
- spuId: 所属SPU
- imgName: 图片名称
- imgUrl: 图片地址
- imgSort: 显示顺序
- defaultImg: 是否默认图
业务场景: iPhone 14 Pro的多角度展示图
---
SkuImagesEntity - SKU图片实体
@TableName("pms_sku_images")
作用: 管理SKU的特定图片
- skuId: 所属SKU
- imgUrl: 图片地址
- imgSort: 排序
- defaultImg: 是否默认图
业务场景: 具体颜色版本的图片
---
🔗 5. 关联关系类
CategoryBrandRelationEntity - 品牌分类关联实体
@TableName("pms_category_brand_relation")
作用: 品牌与分类的多对多关系
- brandId: 品牌ID
- catelogId: 分类ID
- brandName: 品牌名称(冗余存储)
- catelogName: 分类名称(冗余存储)
业务场景: 手机分类下有哪些品牌，品牌下有哪些分类
---
📝 6. 值存储类
ProductAttrValueEntity - 商品属性值实体
@TableName("pms_product_attr_value")
作用: 存储商品的具体属性值
- 商品与属性的多对多关系表
- 存储每个SKU的具体属性值
业务场景: iPhone 14 Pro的具体参数值
---
SkuSaleAttrValueEntity - SKU销售属性值实体
@TableName("pms_sku_sale_attr_value")
作用: 管理SKU的销售属性值
- 颜色、尺寸等影响购买决策的属性
---
SpuInfoDescEntity - SPU描述实体
@TableName("pms_spu_info_desc")
作用: 存储SPU的详细描述信息
- 商品详情页的富文本内容
---
💬 7. 评论系统类
SpuCommentEntity - SPU评论实体
@TableName("pms_spu_comment")
作用: 管理商品评论
- 用户对SPU的评价
- 评分、内容、时间等
---
CommentReplayEntity - 评论回复实体
@TableName("pms_comment_replay")
作用: 管理评论的回复
- 商家或用户的回复内容
---