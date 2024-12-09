## 一、前期准备

1. 运行最新更新的sql文件，对一些不必要的列进行去除，对建表和插数据进行拆分
2. 创建一个新的分支 v1.1后再进行编写

## 二、接口编写

16个接口

### 类目Category相关接口

存在问题的接口：

Delete按钮按后报错“当前分类下关联套餐无法删除”，实际并没有关联套餐



### 菜品Dish相关接口

问题接口（点击后报404、405错误，应该是后台错误）：

<img src="https://i.imgur.com/2UtNTAt.png" alt="image-20241209173727369" style="zoom: 50%;" />

<img src="https://i.imgur.com/QcfLvnr.png" alt="image-20241209174750573" style="zoom:50%;" />

### 套餐Combo相关接口

和Dish问题一样，可以照搬Dish方法解决

<img src="https://i.imgur.com/pknFggp.png" alt="image-20241209174849843" style="zoom:50%;" />

<img src="https://i.imgur.com/i8oC0LO.png" alt="image-20241209175702984" style="zoom:50%;" />

### 订单Order相关接口

约3～4个

## 三、目标与要求

1. 前端按钮交互或输入框交互之后可以观察到正确的结果，后台数据库可以进行正确的更新
2. 用`@Operation`注解对新添加的方法进行正确标注，保证接口文档规范性

## 排查策略

1. 先去找到按钮对应的js方法，再根据js方法中的api映射(`backend/api`文件夹中国)找到后台api，然后进行代码编写和调试

   <img src="https://i.imgur.com/GDMkeeE.png" alt="image-20241209180148436" style="zoom:50%;" />

2. 结合Swagger文档进行系统性的bug排查和代码编写

   <img src="https://i.imgur.com/3Q0lZAY.png" alt="image-20241209175625081" style="zoom:50%;" />

3. 可以结合`/产品原型`文件夹中的示例进行编写