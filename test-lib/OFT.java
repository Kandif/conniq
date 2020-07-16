

package oft;

import conniq.Conniq;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class OFT {
    

    public static void main(String[] args) throws UnknownHostException {
            Conniq c = new Conniq();
            Scanner s = new Scanner(System.in);
            Dane test;
            String client = s.nextLine();
            if(client.equals("0")){
                c.createServer(50009);
                c.waitClient();
                Dane map = new Dane("XD","xd");
                c.sendObjTCP(map);
                System.out.println(map.i);
                for(int i=0;i<5;i++){
                    map.update();
                    System.out.println(map.i);
                    c.sendObjTCP(map);
                }
                System.out.println(c.getLogs()); //wyświetla logi
                c.disconnect();
            }else{
                c.joinServer("localhost", 50009); 
                test =(Dane)c.getObjTCP(0);
                System.out.println(test.i);
                for(int i=0;i<5;i++){
                    test =(Dane)c.getObjTCP(0);
                    System.out.println(test.i);
                }
                System.out.println(c.getLogs()); //wyświetla logi
                c.disconnect();          
            }
            
    }
    
    static class Dane implements Serializable{
        
        private ArrayList<String> tab = new ArrayList<>();
        private String a;
        private String b;
        private int i;
        
        static final long serialversionUID = 1111111;
        
        Dane(String a1,String b1){
            tab.add(a1);
            tab.add(b1);
            a=a1;b=b1;
        }
        
        Dane(Dane d){
            this.a=d.a;
            this.b=d.b;
            this.i=d.i;
        }
        
        void update(){
            i++;
            a=a+i;
            b=b+i;
        }    
        
        public String toString(){
            return "Dane: "+tab.get(0)+" "+tab.get(1);
        }    
        
        Dane get(){
            return this;
        }
                
    }
    
}

class Mapi implements Serializable{
       
    int b=0;


    void update(){
        b++;
    }    
    
}

class Test implements Serializable{
    
    private int x,y;
    
    Test(){
       x=1;y=1; 
    }
    
    public void update(){
        x+=1;
        y+=1;
    }
    
    int getx(){
        return x;
    }
    
    int gety(){
        return y;
    }
}

class Mob implements Serializable{
    float x,y;
    int speed;
    int speedforp;
    float xsp,ysp;
    int times=1;

    Mob(){
       randomPos(); 
    }

    
    public void anim(int sx, int sy,int ex, int ey,int speed){
        x=sx;
        y=sy;
        speedforp=speed;
        this.speed=speed;
        xsp=(ex-sx)/(float)speed;
        ysp=(ey-sy)/(float)speed; 
    }
    
    public void update(){
        if(speed>=0){
            x+=xsp;
            y+=ysp;
            speed-=1;
        }else{
            randomPos();
            times++;
        }
        
    }
    
    Random r = new Random();
    
     public void randomPos(){
        int dir = r.nextInt(4)+1;
        int speedi= r.nextInt(300)+200;
        if(dir==1){
            int y=420;
            int x = r.nextInt(600);
            int fx= r.nextInt(600);
            anim(x, y, fx, -20, speedi);
        }
        else if(dir==2){
            int x=620;
            int y = r.nextInt(400);
            int fy = r.nextInt(400);
            anim(x, y, -20,fy , speedi);
        }
        else if(dir==3){
            int y=-20;
            int x = r.nextInt(600);
            int fx= r.nextInt(600);
            anim(x, y, fx, 420, speedi);
        }
        else if(dir==4){
            int x=-20;
            int y = r.nextInt(400);
            int fy = r.nextInt(400);
            anim(x, y, 620, fy, speedi);
        }
    }
    
}
