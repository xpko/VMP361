package x.vmp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


public class VMP361 {
    private static final Map<String, Method> vmpMethodMap=new ConcurrentHashMap<>();
    private static final Map<Integer,Object[]> argsMap=new ConcurrentHashMap<>();
    private static final Map<Integer,Object> resultMap=new ConcurrentHashMap<>();

    private static final AtomicInteger idGenerator = new AtomicInteger();

    @SuppressWarnings("unchecked")
    public static <R extends Method> R createMethod(Class<R> cls) {
        if (vmpMethodMap.containsKey(cls.getName())) {
            return (R) vmpMethodMap.get(cls.getName());
        }
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            try {
                R result = cls.newInstance();
                vmpMethodMap.put(cls.getName(), result);
                return result;
            } catch (IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        } else {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    R result = cls.newInstance();
                    vmpMethodMap.put(cls.getName(), result);
                } catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return (R) vmpMethodMap.get(cls.getName());
        }
    }

    @SuppressWarnings("unchecked")
    static <R> R exec(String methodName, Object... args){
        Bundle bundle=new Bundle();
        int argsId= idGenerator.incrementAndGet();
        bundle.putInt("vmp_args_id",argsId);
        argsMap.put(argsId,args);
        Method vmp=vmpMethodMap.get(methodName);
        if(vmp==null){
            return null;
        }
        vmp.onCreate(bundle);
        int resultId= bundle.getInt("vmp_result_id");
        Object result= getOrDefault(resultMap,resultId,null);
        argsMap.remove(argsId);
        resultMap.remove(resultId);
        return (R) result;
    }

    static <V> V getOrDefault(Map<Integer, V> sMap, int key, V defValue) {
        V v;
        return (((v = sMap.get(key)) != null) || sMap.containsKey(key))
                ? v
                : defValue;
    }
    @SuppressWarnings("unchecked")
     static <R> R getArg(Bundle bundle,int index,R defValue){
        int argsId=bundle.getInt("vmp_args_id",-1);
        if(argsId==-1)return defValue;
        Object[] args=getOrDefault(argsMap,argsId,new Object[0]);
        if(args==null||args.length-1<index){
            return defValue;
        }
        return (R) args[index];
    }
      static void putResult(Bundle bundle,Object value){
        int resultId=idGenerator.incrementAndGet();
         bundle.putInt("vmp_result_id",resultId);
         resultMap.put(resultId,value);
    }

   public abstract static class Method extends Activity{
        Bundle args;

        protected Method(){

        }
        @SuppressLint("MissingSuperCall")
        @Override
        protected void onCreate(Bundle args){
            this.args=args;
        }
        private String methodName(){
            return this.getClass().getName();
        }
        public  <R> R call(Object... args){
            return VMP361.exec(methodName(),args);
        }
        protected  <R> R getArg(int index){
            return getArg(index,null);
        }
         protected  <R> R getArg(int index,R defValue){
            return VMP361.getArg(args,index,defValue);
        }

        public void result(Object value){
            putResult(args,value);
        }
    }
}
