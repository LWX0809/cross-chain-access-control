import java.util.concurrent.*;

public class test {
    public static void main(String[] args) {
//        ExecutorService executorService= Executors.newSingleThreadExecutor();
//                Future<String> future=executorService.submit(()->
//                           {
//                               //todo:flag=trye
//
//                             return "success";
//                         }
//                 );
//                 try{
//                         String result=future.get(5, TimeUnit.SECONDS);
//                         //String result=future.get(50,TimeUnit.SECONDS);
//                         System.out.println("result:"+result);
//                     }
//                 catch (TimeoutException e){
//                         System.out.println("超时了!");
//                     } catch (InterruptedException e) {
//                     e.printStackTrace();
//                 } catch (ExecutionException e) {
//                     e.printStackTrace();
//                 } finally {
//                     System.out.println("bbbb");
//                     executorService.shutdown();
//                 }

        String collection="false";
        Boolean result=new Boolean(collection);
        if(result){
            System.out.println("aaa");
        }
        else{
            System.out.println("bb");
        }
    }
}
