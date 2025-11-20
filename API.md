# API 接口文档

本文档记录微信小程序服务端提供的所有 API 接口。

## 基础信息

- **服务地址**: `http://127.0.0.1:8080`
- **接口格式**: RESTful API
- **数据格式**: JSON

## 接口列表

### 1. 健康检查接口

**接口地址**: `GET /health`

**接口描述**: 用于检查服务是否正常运行

**请求参数**: 无

**响应示例**:
```
success
```

**响应说明**:
- 返回字符串 "success" 表示服务运行正常

### 2. 数据接口

#### 2.1 数据接口返回格式
所有请求保持以下数据格式，不同接口的data结果不同，2.2开始所有接口只提供data字段的数据说明
**响应字段**:
|字段|类型|示例|说明|
|----|-----|----|
|status|long|0|请求是否成功|
|msg|string|'success'|错误信息|
|data|Object/ObjectList|{'a':1,'b':['10','11','12']}|数据结果，详细示意见2.2|



#### 2.2 地图点位信息

**接口地址**: `POST /map/loadPage`

**接口描述**: 用于返回用户当前页面的地图点位信息

**请求参数**: 

|参数名|类型|示例|说明|
|----|-----|----|----|
|topLeft|String|"{116.443550,39.921900}"|地图左上角经纬度，正数表示东经和北纬，负数表示西经和南纬|
|bottomRight|String|"{116.443750,39.920900}"|地图左上角经纬度，正数表示东经和北纬，负数表示西经和南纬|


**响应说明**:
data类型：ObjectList
Object类型说明
|字段|类型|示例|说明|
|----|-----|----|----|
|coordinate|String|"{116.443750,39.920900}"|点位的坐标|
|name|String|"卢沟桥"|点位名称|
|id|Long|12|点位iD|
|picUrl|String|https://mp.weixin.qq.com/wxopen/basicprofile?action=get_headimg&token=50698586&t=1763474422323|点位的图片链接|


**响应示例**:
```
{
    "status": 0,
    "msg": "success",
    "data": [
        {
            "coordinate": "{116.443750,39.920900}",
            "name": "卢沟桥",
            "id": 12,
            "picUrl": "https://mp.weixin.qq.com/wxopen/basicprofile?action=get_headimg&token=50698586&t=1763474422323"
        },
        {
            "coordinate": "{116.443650,39.921000}",
            "name": "卢沟桥2",
            "id": 13,
            "picUrl": "https://mp.weixin.qq.com/wxopen/basicprofile?action=get_headimg&token=50698586&t=1763474422323"
        }
    ]
}
```

## 接口更新记录

> 每次新增或修改接口时，请在此处记录：
> - 日期
> - 接口名称
> - 变更说明

