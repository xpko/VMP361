package x.vmp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class VMP361 {
    private static final Map<String, Method> vmpMethodMap=new ConcurrentHashMap<>();
    private static final Map<Integer,Object[]> argsMap=new ConcurrentHashMap<>();
    private static final Map<Integer,Object> resultMap=new ConcurrentHashMap<>();

    private static final AtomicInteger idGenerator = new AtomicInteger();

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
    static <V> V getOrDefault(Map<Integer,V> sMap,int key,V defValue){
        V v;
        return (((v = sMap.get(key)) != null) || sMap.containsKey(key))
                ? v
                : defValue;
    }
      static void putResult(Bundle bundle,Object value){
        int resultId=idGenerator.incrementAndGet();
         bundle.putInt("vmp_result_id",resultId);
         resultMap.put(resultId,value);
    }

   public abstract static class Method extends Activity{
        Bundle args;

        protected Method(){
            vmpMethodMap.put(methodName(),this);
        }
        @SuppressLint("MissingSuperCall")
        @Override
        protected void onCreate(Bundle args){
            this.args=args;
        }
        private String methodName(){
            return this.getClass().getName();
        }
        public  <R> R call(){
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
