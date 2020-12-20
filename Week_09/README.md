## 3、（必做）改造自定义RPC的程序，提交到github：

1）尝试将服务端写死查找接口实现类变成泛型和反射

去掉显示配置@Bean，使用@Service更贴近真实使用场景，以OrderServiceImpl为例：

```java
@Service
public class UserServiceImpl implements UserService {

    @Override
    public User findById(int id) {
        return new User(id, "KK" + System.currentTimeMillis());
    }
}
```

DemoResolver新增resolve方法，根据class获取业务实现类：

```java
 
    public Object resolve(Class<?> serviceClass) {
        return this.applicationContext.getBean(serviceClass);
    }
```

服务端invoke方法实现如下，基于klass找到具体业务实现类，封装统一异常RpcfxException。

```java
   public RpcfxResponse invoke(RpcfxRequest request) {
        RpcfxResponse response = new RpcfxResponse();
        String serviceClass = request.getServiceClass();

        try {
            Class klass = Class.forName(request.getServiceClass());

            //done 作业1：改成泛型和反射
            Object service = resolver.resolve(klass);
            Method method = resolveMethodFromClass(service.getClass(), request.getMethod());
            Object result = method.invoke(service, request.getParams()); // dubbo, fastjson,
            // 两次json序列化能否合并成一个
            response.setResult(JSON.toJSONString(result, SerializerFeature.WriteClassName));
            response.setStatus(true);
            return response;
        } catch (ClassNotFoundException|  IllegalAccessException | InvocationTargetException e) {

            // 3.Xstream

            //done 2.封装一个统一的RpcfxException
            // 客户端也需要判断异常
            e.printStackTrace();
            response.setException(new RpcfxException(e.getMessage(),e));
            response.setStatus(false);
            return response;
        }
    }
```



2）尝试将客户端动态代理改成AOP，添加异常处理

使用ByteBuddy字节码增加工具类动态生成代理实例。

```java
public class RpcfxByteBuddy {
    /**
     * 泛型方法，使用ByteBuddy字节码增强生成代理类实例
     *
     * @param serviceClass
     * @param url
     * @param filters
     * @param <T>
     * @return
     */
    public static <T> T create(final Class<T> serviceClass, final String url, Filter... filters) {
        try {
            return (T) new ByteBuddy()
                    .subclass(Object.class)
                    .implement(serviceClass)
                    .intercept(InvocationHandlerAdapter.of(new Rpcfx.RpcfxInvocationHandler(serviceClass, url, filters)))
                    .make()
                    .load(RpcfxByteBuddy.class.getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RpcfxException(e.getMessage(), e);
        }
    }
}
```

在rpcfx-demo-consumer中测试，调用正常。

```java
public static void main(String[] args) {

		UserService userServiceUsingByteBuddy = RpcfxByteBuddy.create(UserService.class, "http://localhost:8089/");
		User userUsingByteBuddy = userServiceUsingByteBuddy.findById(1);
		System.out.println("find user id=1 from server: " + userUsingByteBuddy.getName());

		OrderService orderService = Rpcfx.create(OrderService.class, "http://localhost:8089/");
		Order order = orderService.findOrderById(1992129);
		System.out.println(String.format("find order name=%s, amount=%f",order.getName(),order.getAmount()));

	}
```

3）尝试使用Netty+HTTP作为client端传输方式

待完成

## 3、（必做）结合dubbo+hmily，实现一个TCC外汇交易处理，代码提交到github：

待完成

1）用户A的美元账户和人民币账户都在A库，使用1美元兑换7人民币；

2）用户B的美元账户和人民币账户都在B库，使用7人民币兑换1美元；

3）设计账户表，冻结资产表，实现上述两个本地事务的分布式事务。