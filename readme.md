# VMP361

For details, refer
to [如何充分发挥360加固onCreate的VMP保护特性](https://note.shlu.fyi/Android/%E5%A6%82%E4%BD%95%E5%85%85%E5%88%86%E5%8F%91%E6%8C%A5360%E5%8A%A0%E5%9B%BAonCreate%E7%9A%84VMP%E4%BF%9D%E6%8A%A4%E7%89%B9%E6%80%A7/)

## Import

Step 1. Add it in your root build.gradle at the end of repositories:
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Or add it in your settings.gradle.kts at the end of repositories:

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven{
            url=uri("https://jitpack.io")
        }
    }
}
```

Step 2. Add the dependency in your app build.gradle
```groovy
implementation 'com.github.xpko:VMP361:1.4'
```

Or add the dependency in your app build.gradle.kts

```groovy
implementation("com.github.xpko:VMP361:1.4")
```

[![](https://jitpack.io/v/xpko/VMP361.svg)](https://jitpack.io/#xpko/VMP361)

## Use

直接继承其中的VMP361.Method类，把要保护的核心代码放入onCreate方法，然后在 AndroidManifest.xml 里注册即可

```java
public class Request extends VMP361.Method {

    @Override
    protected void onCreate(Bundle args) {//这里建议设为 protected， 防止被外部调用
        super.onCreate(args);//调用父类的onCreate解析参数
        String urlString = "https://note.shlu.fyi?arg0="+getArg(0);//获取参数
        StringBuilder result = new StringBuilder();
        HttpsURLConnection urlConnection = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000); // 10秒
            urlConnection.setReadTimeout(10000); // 10秒

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                in.close();
            } else {
                result.append("响应码：").append(responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.append("异常：").append(e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        result(result);//返回结果
    }

}
```

其中super.onCreate(args)调用父类的onCreate，参数交给父类解析，然后就可以用getArg(0)
可以获取对应索引的参数，返回值为泛型，可以自动转换类型，result(result)将处理后的结果返回

接下来就可以调用了

```java
public class Main extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button request= findViewById(R.id.request);
        Button hook= findViewById(R.id.hook);
        Button code= findViewById(R.id.code);
        request.setOnClickListener(v->{
            new Thread(()-> System.out.println("qqqq:"+VMP361.createMethod(Request.class).call("aaa"))).start();
        });
    }
}
```

一行代码搞定：

```java
VMP361.createMethod(Request.class).call("aaa")
```
