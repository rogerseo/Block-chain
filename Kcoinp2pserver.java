import java.io.*;                   // Server Socket
import java.net.*;                 

// What is Server

// 1. Receive transaction of other Node and save
// 2. Receive Block of other Node and save

public class Kcoinp2pserver {
		
	public static transaction[] tran_list0 = new transaction[1000];   // Save 1000 Transaction List. Do not put this in the creator
	public static transaction[] tran_list1 = new transaction[1000];    
	public static transaction[] tran_list2 = new transaction[1000]; 
	public static transaction[] tran_list3 = new transaction[1000];
	public static transaction[] tran_list4 = new transaction[1000];
	public static transaction[] tran_list5 = new transaction[1000];
		
	public static int tran_count0 = 0;                               //  How many transaction List in 0~10 minute  
	public static int tran_count1 = 0;                               //  10~20 minute 
	public static int tran_count2 = 0;                               //  20~30 minute 
	public static int tran_count3 = 0;                               //  30~40 minute 
	public static int tran_count4 = 0;                               //  40~50 minute 
	public static int tran_count5 = 0;                               //  50~60 minute 
	
	public static boolean tran_reset0 = false;                       //  Reset transaction List in 0~10 minute 
	public static boolean tran_reset1 = false;                       //  10~20 minute 
	public static boolean tran_reset2 = false;                       //  10~20 minute 
	public static boolean tran_reset3 = false;                       //  10~20 minute 
	public static boolean tran_reset4 = false;                       //  10~20 minute 
	public static boolean tran_reset5 = false;                       //  10~20 minute 
		
	public static Block[] block_list0 = new Block[1000];             //  Save 1000 Blocks from other node 
	public static Block[] block_list1 = new Block[1000];             
	public static Block[] block_list2 = new Block[1000];
	public static Block[] block_list3 = new Block[1000];
	public static Block[] block_list4 = new Block[1000];
	public static Block[] block_list5 = new Block[1000];
		
	public static int block_count0 = 0;                              // Block count  
	public static int block_count1 = 0;
	public static int block_count2 = 0;
	public static int block_count3 = 0;
	public static int block_count4 = 0;
	public static int block_count5 = 0;
	
	public Kcoinp2pserver() {      // Creator 	
    }
    
	
	public static void main(String[] args) {
		
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	    //Initialize values	
		
		for (int i = 0; i < 1000; i++) {    	 
               
			   tran_list0[i] = new transaction();
               tran_list1[i] = new transaction();
               tran_list2[i] = new transaction();
               tran_list3[i] = new transaction();
               tran_list4[i] = new transaction();
               tran_list5[i] = new transaction();
	   	     } 		        		   	  		        			   
		
		for (int i = 0; i < 1000; i++)   {           
               
			   block_list0[i] = new Block();
               block_list1[i] = new Block();
               block_list2[i] = new Block();
               block_list3[i] = new Block();
               block_list4[i] = new Block();
               block_list5[i] = new Block();   
	   	     
		     } 		       
				
		if (args.length < 1) return;                                  // Close if there is no args(Port)
		 
	        int port = Integer.parseInt(args[0]);
	        
	        try (ServerSocket serverSocket = new ServerSocket(port)) {

	            System.out.println("P2P Server is listening on port " + port);

	            
	            while (true) {  
	            	
	            		        	            	            	
	            	Socket socket = serverSocket.accept();
	                System.out.println("지갑1 수신 완료");
	                new ServerThread(socket).start();                

	                 
	              
	                if (tran_reset0) {
	             	    
       		   	         for (int i = 0; i < 1000; i++) {  		        		   	    	 
       		   	    	 
       	                             tran_list0[i].reset();   
       		   	         } 	
       		   	         tran_reset0 = false;
	                }
	                
	                else if (tran_reset1) {
	             	    
      		   	         for (int i = 0; i < 1000; i++) {  		        		   	    	 
      		   	    	 
      	                             tran_list1[i].reset();   
      	                 } 		
      		   	         tran_reset1 = false;
	                }
	                
	                else if (tran_reset2) {
	             	    
     		   	         for (int i = 0; i < 1000; i++) {  		        		   	    	 
     		   	    	 
     	                             tran_list2[i].reset();   
     	                 }
     		   	         tran_reset2 = false;
	                }
	                
	                else if (tran_reset3) {
	             	    
    		   	         for (int i = 0; i < 1000; i++) {  		        		   	    	 
    		   	    	 
    	                             tran_list3[i].reset();   
    	                 } 		             
    		   	         tran_reset3 = false;
	                }
	                
	                else if (tran_reset4) {
	             	    
   		   	             for (int i = 0; i < 1000; i++) {  	        		   	    	 
   		   	    	 
   	                             tran_list4[i].reset();   
   	                      } 		
   		   	              tran_reset4 = false;
	                }
	                
	                else if (tran_reset5) {
	             	    
  		   	             for (int i = 0; i < 1000; i++) {  		        		   	    	 
  		   	    	 
  	                             tran_list5[i].reset();   
  	                      } 		 
  		   	              tran_reset5 = false;
	                }   
	            
	            }	            
	        } catch (IOException ex) {
	            System.out.println("Server exception: " + ex.getMessage());
	            ex.printStackTrace();
	        }		        
  	 
	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~			
	}	
}



class ServerThread extends Thread {
	 
	private Socket socket;
	ObjectInputStream block_tran; 	  
	
	
	public ServerThread(Socket socket)throws IOException  {
		this.socket = socket;
        block_tran = new ObjectInputStream(socket.getInputStream());     
	}
	
    public void run() {
    	
    	try {
             
             Object o = (Object)block_tran.readObject();   
             
             char c = o.toString().charAt(0);
                                
                        
             
             if (c == 't') {
             
              System.out.println("거래 내역을 수신 받았습니다.");
            	 
              transaction tran_temp = (transaction)o;
                         
              String a = tran_temp.valueOftimestamps(); 
              
              String b = a.substring(14,16);     
              
              int tran_time =Integer.parseInt(b);
                                          
              if ( tran_time >= 0 && tran_time <10 ) {
            	  
            	      Kcoinp2pserver.tran_list0[Kcoinp2pserver.tran_count0] = tran_temp;
            	               	                 	      
            	      Kcoinp2pserver.tran_count0++;                  
            	      
            	      System.out.println("0분~10분까지 거래 저장 수:" + Kcoinp2pserver.tran_count0);
            	      
                      }
              
              else if ( tran_time >= 10 && tran_time <20) {
        	     
            	      Kcoinp2pserver.tran_list1[Kcoinp2pserver.tran_count1] = tran_temp;            	      
            	      
            	      Kcoinp2pserver.tran_count1++;
            	      System.out.println("10분~20분까지 거래 저장 수:" + Kcoinp2pserver.tran_count1);
                      }
              
              else if ( tran_time >= 20 && tran_time <30) {
                  
        	          Kcoinp2pserver.tran_list2[Kcoinp2pserver.tran_count2] = tran_temp;
        	          
           	          Kcoinp2pserver.tran_count2++;
        	          System.out.println("20분~30분까지 거래 저장 수:" + Kcoinp2pserver.tran_count2);
                      }
              
        
              else if ( tran_time >= 30 && tran_time <40) {
    	          
            	      Kcoinp2pserver.tran_list3[Kcoinp2pserver.tran_count3] = tran_temp;
            	      
            	      
            	      Kcoinp2pserver.tran_count3++;
            	      System.out.println("30분~40분까지 거래 저장 수:" + Kcoinp2pserver.tran_count3);
                      }
              
              else if ( tran_time >= 40 && tran_time <50) {
                  
        	          Kcoinp2pserver.tran_list4[Kcoinp2pserver.tran_count4] = tran_temp;
        	          
        	          
        	          Kcoinp2pserver.tran_count4++;
        	          System.out.println("40분~50분까지 거래 저장 수:" + Kcoinp2pserver.tran_count4);
                      }
              
              else if ( tran_time >= 50 && tran_time <60) {
            	  
        	          Kcoinp2pserver.tran_list5[Kcoinp2pserver.tran_count5] = tran_temp;
        	           
        	          
        	          Kcoinp2pserver.tran_count5++;
        	          System.out.println("50분~60분까지 거래 저장 수:" + Kcoinp2pserver.tran_count5);
                      }             
             }        
             
             
             else if ( c == 'B') {
            	     
            	     System.out.println("Block을 수신 받았습니다.");               	 
            	     Block block_tmp = (Block)o;
            	     
            	                 	     
                     String a = block_tmp.valueOftimestamps();              
                     String b = a.substring(14,16);              
                     
                     //System.out.println(a);
                     //System.out.println(b);
                     
                     
                     int block_time =Integer.parseInt(b);
                     
                     
                     if ( block_time >= 0 && block_time <10 ) {
                   	  
                   	      Kcoinp2pserver.block_list0[Kcoinp2pserver.block_count0] = block_tmp;
                   	               	      
                   	      //System.out.println(Kcoinp2pserver.block_count0);
                   	      Kcoinp2pserver.block_count0++;                  
                   	      System.out.println("0분~10분 총 수신 Block 수:" + Kcoinp2pserver.block_count0);                   	      
             
                          }
                                          
                     else if ( block_time >= 10 && block_time <20) {
               	     
                   	      Kcoinp2pserver.block_list1[Kcoinp2pserver.block_count1] = block_tmp;
                   	      
                   	      // System.out.println(Kcoinp2pserver.block_count1);
                   	      Kcoinp2pserver.block_count1++;
                   	      System.out.println("10분~20분 총 수신 Block 수:" + Kcoinp2pserver.block_count1);
                             }
                     
                     
                     else if ( block_time >= 20 && block_time <30) {
                         
               	          Kcoinp2pserver.block_list2[Kcoinp2pserver.block_count2] = block_tmp;
               	          
               	          //System.out.println(Kcoinp2pserver.block_count2);
               	          Kcoinp2pserver.block_count2++;
               	          System.out.println("20분~30분 총 수신 Block 수:" + Kcoinp2pserver.block_count2);
                          }
                     
               
                     else if ( block_time >= 30 && block_time <40) {
           	          
                   	      Kcoinp2pserver.block_list3[Kcoinp2pserver.block_count3] = block_tmp;
                   	      
                   	      // System.out.println(Kcoinp2pserver.block_count3);
                   	      Kcoinp2pserver.block_count3++;
                   	      System.out.println("30분~40분 총 수신 Block 수:" + Kcoinp2pserver.block_count3);
                          }
                     
                     else if ( block_time >= 40 && block_time <50) {
                         
               	          Kcoinp2pserver.block_list4[Kcoinp2pserver.block_count4] = block_tmp;
               	          
               	          // System.out.println(Kcoinp2pserver.block_count4);
               	          Kcoinp2pserver.block_count4++;
               	          System.out.println("40분~50분 총 수신 Block 수:" + Kcoinp2pserver.block_count4);
                          }
                     
                     else if ( block_time >= 50 && block_time <60) {
                   	  
               	          Kcoinp2pserver.block_list5[Kcoinp2pserver.block_count5] = block_tmp;
               	           
               	          // System.out.println(Kcoinp2pserver.block_count5);
               	          Kcoinp2pserver.block_count5++;
               	          System.out.println("50분~60분 총 수신 Block 수:" + Kcoinp2pserver.block_count5);
                          }            	     
             
             }
             
             System.out.println("");                           
             socket.close();                
              
             } 
    	     catch (IOException ex) {
                      System.out.println("Server exception: " + ex.getMessage());
                      ex.printStackTrace();        
    	     }
    	
            catch (ClassNotFoundException e){
                      System.err.println(e); 
             }
    }
}