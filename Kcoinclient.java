import java.io.*;
import java.net.*;

import java.security.MessageDigest; // HASH Code

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import java.awt.Button;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.ImageObserver;

import javax.imageio.ImageIO;

import java.awt.Image;
import javax.swing.*;

// P2P 통신에서 송신 / 암호화 / 계좌 update / Block 생성 역활을 함.

public class Kcoinclient {                      
	
    private Frame mainFrame;
    private Label headerLabel;
    private Label statusLabel;
    private Label from_wallet;
    private Label to_wallet;
    private Label coin;
    
    private Panel controlPanel;
    
    private static Block[] block= new Block[100000000];
    private static transaction tran_info;
    
    private static String hostname;
    private static int port;
       

    public Kcoinclient() {                 // 생성자에서 초기화 시킴
        
    	prepareGUI();
  
    	// 프로그램 편의를 위해 Block의 총 수를 결정한다. 
        // 원안은 파일에 Block정보를 저장 함으로 시스템 메모리에는 현재 Block 정보만 필요하다.
        // 전체 Block을 모두 메모리로 가지고 오는 건 불필요 하다.          
    }
   
        
	public static void main(String[] args) {  
	
		     // Block 초기화 필요/시간 설정 필요/ 보낼때 모든 Block 다 보내야 하나?? No!!! 지금 Block 만
				
		     Kcoinclient awtControlDemo = new Kcoinclient();
             awtControlDemo.showButton();
             
		     //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		     // Make a connection to other Peer to broadcast 
		  	
		     if (args.length < 2) return;
	         hostname = args[0];	   
	         port = Integer.parseInt(args[1]);	      
	         
	         //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	         // SHA-256 암호화함.  Base를 암호화 할 수 있는 기 술
 	         
	         String Acount = "aaa";
	         try{
	 
	            MessageDigest digest = MessageDigest.getInstance("SHA-256");
	            byte[] hash = digest.digest(Acount.getBytes("UTF-8"));
	            StringBuffer hexString = new StringBuffer();
	 
	            for (int i = 0; i < hash.length; i++) {
	                String hex = Integer.toHexString(0xff & hash[i]);
	                if(hex.length() == 1) hexString.append('0');
	                hexString.append(hex);
	            }
	 
	            System.out.println(hexString.toString());
	 
	        } catch(Exception ex){
	            throw new RuntimeException(ex);
	        }       	     
	         
	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~	        
	        //Time 싱크를 초기화 함 	         	         	         
	         
	        int current_state = 0 ; // 0은 garship state 1은 결정 state
	        String current_minute ="00" ;  //초기화  
	        
	        //인덱스를 싱크 초기화 함
	        int index = 0 ;    // 프로그램 수행 전, 몇번째 Block 인지 확인 한다.
	        // 마지막 Block을 읽어 오고 그 인덱스를 확인해 야한다. 마지막 Block은 파일에 있다.
	        // 읽어 온 Block을 다른 시간이 맞지 않다면 다른 사람과도 비교 해야 한다.
	        // 여기서는 Block에서 읽어 왔고 읽어 온 결과가 0 이라고 가정한다.	        	      
	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	        // 상단에 모든 초기화를 완료 한다.
	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	        // Block 생성 알고리즘 수행( 거래 내역을 수신하여 현재 Block에 업데이트 / 13분 결정 시간에 결정하여 Broadcast / 14분에 결정된 Block을 저장함)		        
	      	        
	        while (true) {      //변수 할당을 최대한 줄인다.	        	
	        	
	        	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	        	//시간 확인
	        	
	        	
	        	Date date = new Date();   //while 문 안으로 들어와야 한다. 안그러면 시간 update가 안된다.
	        	DateFormat df = new SimpleDateFormat("mm");  // 분만 뽑안 낸다.	 	    
	        	// DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss (z Z)");
	        	TimeZone time = TimeZone.getTimeZone("Asia/Seoul");
	 	        df.setTimeZone(time);	

	 	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	        	//1. 거래 내역을 실시간으로 수신하여 현재 Block에 update 	
	 	        //2. 13분 결정 시간에 현재까지 거래내역을 update하여 Broadcast
	 	        //3. Transaction 저장된 내용 Reset 함.(미 구현중)
	 	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~	        		 	        
	 	        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 	 	         	     	 	        
	 	        
	 	        if ( df.format(date).equals("03") )
		        {	
	 	   	 		
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;    // 거래 내역 전송 시간
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               		       
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();            //객체에 반드시 값을 준다 안그러면 Null Ponit 발생
		        		             		        		           
		        		         System.out.println("몇번째 블락:" + index);
		        		                 		        		        		     		        		   	     
		        		         for (int i = 0; i < 1000; i++) {  //1억을 Block에 할당하고 하나씩 Reset 함.
		        		   	    	 
		        	                 
		        		        	 block[index].tran_list[i] =  Kcoinp2pserver.tran_list0[i];
		        	                 
		        	                 // Block에 거래 내용을 다 저장 하고 거래 내용 저장소를 Reset 해야 하는데....    
		        		         }
		        		   	     
		        		         Kcoinp2pserver.tran_reset0 = true;
		        		         
		        		         
		        		         block_out.writeObject(block[index]);    
		        		         block_out.flush();           // flush
		        	             } catch (UnknownHostException ex) {
		        		            System.out.println("Server not found: " + ex.getMessage());
		        		         } catch (IOException ex) {
		        		            System.out.println("I/O error: " + ex.getMessage());
		        		         }
		        	        
		        	         index++;  //   		        	         
		        	         
		        	         System.out.println("");
			        		 System.out.format("%s%n%s%n%n", time.getDisplayName(),df.format(date));
			        		 System.out.println("모든 노드에 Block 전송");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		        	}          
		        }

	 	       if ( df.format(date).equals("13") )
		        {	
	 	   	 		
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;    // 거래 내역 전송 시간
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               		       
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();            //객체에 반드시 값을 준다 안그러면 Null Ponit 발생
		        		             		        		           
		        		         System.out.println("몇번째 블락:" + index);
		        		         
		        		        	    
		        		   	     for (int i = 0; i < 1000; i++) {  //1억을 Block에 할당하고 하나씩 Reset 함.		        		   	    	 
		        		   	    	 
		        	                 block[index].tran_list[i] =  Kcoinp2pserver.tran_list1[i];    // - 수락 전  + 수락 함                            	      
		        	                 //Kcoinp2pserver.tran_list1[i].reset(); // java가 다르면 접근 불가   
		        	                 		             		         		             
		        		   	     } 		        		   	  		        		           		        		         
		        		        		 
		        		   	     Kcoinp2pserver.tran_reset1 = true;
		        		   	     
		        		         block_out.writeObject(block[index]);
		        		       
		        		         block_out.flush();           // flush	         
		        		                 	         
		        	             } catch (UnknownHostException ex) {
		        		            System.out.println("Server not found: " + ex.getMessage());
		        		         } catch (IOException ex) {
		        		            System.out.println("I/O error: " + ex.getMessage());
		        		         }
		        	         index++;  //   		        	         
		        	         
		        	         System.out.println("");
			        		 System.out.format("%s%n%s%n%n", time.getDisplayName(),df.format(date));
			        		 System.out.println("모든 노드에 Block 전송");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		        	}          
		        }
	 	        	 	        
	 	       if ( df.format(date).equals("23") )
		        {	
	 	   	 		
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;    // 거래 내역 전송 시간
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();            //객체에 반드시 값을 준다 안그러면 Null Ponit 발생
		        		             		        		           
		        		         System.out.println("몇번째 블락:" + index);
		        		         
		        		   	     for (int i = 0; i < 1000; i++) {  //1억을 Block에 할당하고 하나씩 Reset 함.
		        		   	    	 
		        	                 block[index].tran_list[i] =  Kcoinp2pserver.tran_list2[i];    // - 수락 전  + 수락 함                                                         //  System.out.format("%d = %s%n", i, wallet[i]); // 출력하기   	      
		         		             
		        	                 
		        	                  
		        		   	     } 		        		   	  		        		           		        		         
		        		        		 
		        		   	     Kcoinp2pserver.tran_reset2 = true;
		        		   	   
		        		         block_out.writeObject(block[index]);
		        		       		        		         
		        		         block_out.flush();           // flush	         
		        		                 	         
		        	             } catch (UnknownHostException ex) {
		        		            System.out.println("Server not found: " + ex.getMessage());
		        		         } catch (IOException ex) {
		        		            System.out.println("I/O error: " + ex.getMessage());
		        		         }
		        	         index++;  //   		        	         
		        	         
		        	         System.out.println("");
			        		 System.out.format("%s%n%s%n%n", time.getDisplayName(),df.format(date));
			        		 System.out.println("모든 노드에 Block 전송");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		        	}          
		        }
	 	         	 	       
	 	       if ( df.format(date).equals("33") )
		        {	
	 	   	 		
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;    // 거래 내역 전송 시간
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();            //객체에 반드시 값을 준다 안그러면 Null Ponit 발생
		        		             		        		           
		        		         System.out.println("몇번째 블락:" + index);
		        		        
		        		        		        		     
		        		         
		        		   	     for (int i = 0; i < 1000; i++) {  //1억을 Block에 할당하고 하나씩 Reset 함.
		        		   	    	 
		        	                 block[index].tran_list[i] =  Kcoinp2pserver.tran_list3[i];    // - 수락 전  + 수락 함                                                         //  System.out.format("%d = %s%n", i, wallet[i]); // 출력하기   	      
		         		             
		        	                        
		        		   	     } 		        		   	  		        		           		        		         
		        		        	
		        		   	     Kcoinp2pserver.tran_reset3 = true;
		        		   	   
		        		   	   
		        		         block_out.writeObject(block[index]);
		        		       		         
		        		         block_out.flush();           // flush	         
		        		                 	         
		        	             } catch (UnknownHostException ex) {
		        		            System.out.println("Server not found: " + ex.getMessage());
		        		         } catch (IOException ex) {
		        		            System.out.println("I/O error: " + ex.getMessage());
		        		         }
		        	         index++;  //   		        	         
		        	         
		        	         System.out.println("");
			        		 System.out.format("%s%n%s%n%n", time.getDisplayName(),df.format(date));
			        		 System.out.println("모든 노드에 Block 전송");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		        	}          
		        } 
	 	        
	 	       
	 	       if ( df.format(date).equals("43") )
		        {	
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;    // 거래 내역 전송 시간
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();            //객체에 반드시 값을 준다 안그러면 Null Ponit 발생
		        		             		        		           
		        		         System.out.println("몇번째 블락:" + index);
		        		       
		        		   	     for (int i = 0; i < 1000; i++) {  //1억을 Block에 할당하고 하나씩 Reset 함.
		        		   	    	 
		        	                 block[index].tran_list[i] =  Kcoinp2pserver.tran_list4[i];    // - 수락 전  + 수락 함                                                           	      
		        	                 		         		             		        	                 		        		   	   
		        		   	     }
		        	                 
		        		   	     Kcoinp2pserver.tran_reset4 = true;
		        		   	   
		        		         block_out.writeObject(block[index]);		        		         
		        		         block_out.flush();           // flush	         
		        		                 	         
		        	             } catch (UnknownHostException ex) {
		        		            System.out.println("Server not found: " + ex.getMessage());
		        		         } catch (IOException ex) {
		        		            System.out.println("I/O error: " + ex.getMessage());
		        		         }
		        		         
		        	         index++;  //   		        	         
		        	         
		        	         System.out.println("");
			        		 System.out.format("%s%n%s%n%n", time.getDisplayName(),df.format(date));
			        		 System.out.println("모든 노드에 Block 전송");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		           }          
		        } 	        
	 	        
	 	
	 	       if ( df.format(date).equals("53") )
		        {	
	 	   	 		
		        	if (!df.format(date).equals(current_minute)) {
		        		
		        		current_state= 1;    // 거래 내역 전송 시간
	            	        		        	
		        	        try (Socket socket = new Socket(hostname, port)) {
		   	 	        	    	         	               
		        		         ObjectOutputStream block_out = new ObjectOutputStream(socket.getOutputStream());	         		
		        		                		         		        		         		        		         
		        		         block[index] = new Block();            //객체에 반드시 값을 준다 안그러면 Null Ponit 발생
		        		             		        		           
		        		         System.out.println("몇번째 블락:" + index);
		        		         		        		        		        		     		        		         
		        		   	     for (int i = 0; i < 1000; i++) {  //1억을 Block에 할당하고 하나씩 Reset 함.
		        		   	    	 
		        	                 block[index].tran_list[i] =  Kcoinp2pserver.tran_list5[i];    // - 수락 전  + 수락 함                                                         //  System.out.format("%d = %s%n", i, wallet[i]); // 출력하기   	      
		        	                 //Kcoinp2pserver.tran_list5[i].reset();
		        		   	     } 		        		   	  		        		           		        		         
		        		        	
		        		   	     Kcoinp2pserver.tran_reset5 = true;
		        		   	   
		        		   	     
		        		         block_out.writeObject(block[index]);
		        		      
		        		         block_out.flush();           // flush	         
		        		                 	         
		        	             } catch (UnknownHostException ex) {
		        		            System.out.println("Server not found: " + ex.getMessage());
		        		         } catch (IOException ex) {
		        		            System.out.println("I/O error: " + ex.getMessage());
		        		         }
		        	         
		        	         index++;  //   		        	         
		        	         
		        	         System.out.println("");
			        		 System.out.format("%s%n%s%n%n", time.getDisplayName(),df.format(date));
			        		 System.out.println("모든 노드에 Block 전송");
			        	
			        		 System.out.println("");
			        		 System.out.println("");
			        		 System.out.println("");		        		 
		        	}          
		        }
	 	        
	 	       
	 	    	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	        	//3. 14분 저장 시간에 저장소에 저장 필요
	 	       
	 	        current_minute = df.format(date);      // 시간 업데이트
	 	        
	 	        //break;       	        
	            
	        }   //while 문 종료    
	}           // Main method 종료	
	
	
	   private void prepareGUI() {
	        // Frame 에 대한 셋팅
	        mainFrame = new Frame("Achive");
	        mainFrame.setSize(400, 400);
	        mainFrame.setLayout(new GridLayout(3, 1));
	        mainFrame.addWindowListener(new WindowAdapter() {
	            public void windowClosing(WindowEvent windowEvent) {
	                System.exit(0);
	            }
	        });
	        
	        Toolkit toolkit = mainFrame.getToolkit();
	        Image image = toolkit.createImage("C:\\Users\\yu\\eclipse-workspace\\1.png");
	        mainFrame.setIconImage(image);

	        // 상단에 있는 라벨
	        headerLabel = new Label();
	        headerLabel.setAlignment(Label.CENTER);
	        headerLabel.setText("Coin Transaction");
	       
	        // 하단 상태값 라벨
	        statusLabel = new Label();
	        statusLabel.setText("Status Lable");
	        statusLabel.setAlignment(Label.CENTER);
	        statusLabel.setSize(350, 100);
	 
	        controlPanel = new Panel();
	        controlPanel.setLayout(new FlowLayout());
	 
	        mainFrame.add(headerLabel);
	        mainFrame.add(controlPanel);
	        mainFrame.add(statusLabel);
	        
	        mainFrame.setVisible(true);
	    }
	   
	   private void showButton() {
		   
	        Button btnOk = new Button("계좌확인");
	        Button btnSubmit = new Button("Submit");
	        Button btnCancel = new Button("Cancel");
	         
	        TextField t1,t2, t3;  
	        t1=new TextField("");  
	        t1.setBounds (0,0, 200,30); 
	        t1.setColumns(19);
	        t2=new TextField("");  
	        t2.setBounds (0,0, 200,30); 
	        t2.setColumns(19);
	        t3=new TextField("");  
	        t3.setBounds (50,100, 200,30);  
	        t3.setColumns(16);	        
	        
	        
	        from_wallet = new Label();
	        //from_wallet.setAlignment(Label.CENTER);
	        from_wallet.setText("나의 지갑");
	        
	        to_wallet = new Label();
	        //to_wallet.setAlignment(Label.CENTER);
	        to_wallet.setText("상대 지갑");
	        
	        coin = new Label();
	        //coin.setAlignment(Label.CENTER);
	        coin.setText("Coin");
	        
	        btnOk.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                statusLabel.setText("계좌가 확인 되었습니다");
	            }
	        });
	             	        	        
	        btnSubmit.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                   
	                tran_info = new transaction();
	                
	                tran_info.setwalletfrom(Integer.parseInt(t1.getText()));  //t1의 Text 값을 받고 다시 int로 변환
	                tran_info.setwalletto(Integer.parseInt(t2.getText()));
	                tran_info.setamount(Integer.parseInt(t3.getText())); 
	                
        	        try (Socket socket = new Socket(hostname, port)) {
	   	 	        		         	               
        	        	ObjectOutputStream tran_out = new ObjectOutputStream(socket.getOutputStream());	         		
       		       	 	        
        	        	Date date1 = new Date();   //while 문 안으로 들어와야 한다. 안그러면 시간 update가 안된다. 	    
        	        	DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss (z Z)");
        	        	TimeZone time1 = TimeZone.getTimeZone("Asia/Seoul");
        	        	df1.setTimeZone(time1);	
 	        		
        	        	tran_info.settimestamps(df1.format(date1));
       		           		        
        	        	System.out.println(tran_info.valueOftimestamps());
        	        	
        	        	System.out.println("transaction 객체 전송");
        	        	       	        	
        	        	tran_out.writeObject(tran_info);
       		       
        	        	tran_out.flush();           // flush	         
       		         	        	         
       		         } catch (UnknownHostException ex) {
       		            System.out.println("Server not found: " + ex.getMessage());
       		         } catch (IOException ex) {
       		            System.out.println("I/O error: " + ex.getMessage());
       		         }
       	       	            	        
       	             System.out.println("");
	        		 System.out.println("거래 내용 전송");
	        	     System.out.println("");
	        	     
	                 try {
	                	 statusLabel.setText("전송 중....");  
	                     Thread.sleep(3000);
	                      
	                  } catch (InterruptedException ie) {
	                      ie.printStackTrace();
	                  }
	        	     
	        	     statusLabel.setText("전송이 완료 되었습니다");  
	        	     
	            }
	        });
	          
	        controlPanel.add(t1);
	        controlPanel.add(from_wallet);
	        controlPanel.add(t2);
	        controlPanel.add(to_wallet);
	        controlPanel.add(t3);
	        controlPanel.add(coin);
	        controlPanel.add(btnSubmit);	        
	        mainFrame.setVisible(true);        
	   }	   
}



class Block implements Serializable{         // 직열화 전송으 위해서 필요함.
	
	  private int index ;                    // 몇 번째 Block 인지 확인
	  
	  private String pre_hash;               // pre-block에 대한 Hash 값
	  
	  private String this_hash;              // 이 블락의 최종 해쉬         
	  
	  private String timestamps;             // 현재 시간
	  
	  private int wallet[] = new int [3000];      // 계좌 30000개 생성
	  
	  public transaction[] tran_list = new transaction[1000];    // 10만개 거래 내용을 저장. 구성자 안으로 넣으면 Null point 발생
	  
	  public Block() {
		  
      index = 0;
      pre_hash ="";
      this_hash="";
      timestamps="";

  	  for (int i = 0; i < wallet.length; i++) {
  		           wallet[i] = -i;                       // - 수락 전  + 수락 함                                                         //  System.out.format("%d = %s%n", i, wallet[i]); // 출력하기   	      
  		       } 
	  
  	  for (int i = 0; i < 1000 ; i++) {
	               tran_list[i] = new transaction();                       // - 수락 전  + 수락 함                                                         //  System.out.format("%d = %s%n", i, wallet[i]); // 출력하기   	      
	           } 
      }
	  
	  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	  //값을 확인 가능
	  
	  public int valueOfindex(){
		     return index;
      }
	  	  
	  public String valueOfpre_prehash(){
		    return pre_hash;
      }
	  
	  public String valueOfthis_thishash(){
		     return this_hash;
      }
	  
	  public String valueOftimestamps(){
		     return timestamps;
      }
	   
   	  public int valueOfwallet(int w){
		     return wallet[w];
      }
   	  
      //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
   	  //값을 setting 가능
   	  
   	  public void setindex(int a){
		     index= a; 
		     }
	  
	  public void setprehash(){
		     pre_hash= "";
             }
	  
	  public void setthishash(){
		     this_hash= "";
      }
	  
	  public void settimestamps(String a){
		     timestamps= a;
      }
	  
	  public void setwallet(int w){
		     wallet[w] = 0 ;
      }
	  
	  public void tran_a_to_b(int a, int b , int c){     //계좌 이체
		     wallet[a] = wallet[a] - c ;
		     wallet[b] = wallet[b] + c ;
      }
	  
	  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	  //값 전송 가능
}


class transaction implements Serializable{   // 직열화 전송을 위해서 필요함.
		
	  private int wallet_from;               //주의!!static 변수는 보낼 객체로 안보내 진다.
	  
	  private int wallet_to;
	  
	  private int amount; 
	 
	  private String timestamps;
	  
	  private String pre_hash;               // pre block에 대한 Hash 값
	  
	  private String this_hash;              // 이 블락의 최종 해쉬         
	  	  
	  public transaction() {
		  
		     wallet_from = 0;
		     wallet_to = 0; 
		     amount = 0;
		     timestamps="";  
             pre_hash ="";
             this_hash="";

	  }
	 
	  public void reset(){		  
		     wallet_from = 0;
		     wallet_to = 0; 
		     amount = 0;
		     timestamps="";  
             pre_hash ="";
             this_hash="";
      }

	  
	  public void setwalletfrom(int a){
		     wallet_from = a;
      }
	  
	  public void setwalletto(int a){
		     wallet_to = a;
      }
	  
	  public void setamount(int a){
		     amount = a;
      }
	  
	  public void settimestamps(String a){
		     timestamps = a ;
      }
	  
	  public void setprehash(String a){
		     pre_hash= a ;
      }
	  
	  public void setthishash(String a){
		     this_hash = a ;
      }
	  
	  public int valueOfwalletfrom(){
		     return wallet_from ;
      }
	  
	  public int valueOfwalletto(){
		     return wallet_to ;
      }
	  
	  public int valueOfamount(){
		     return amount ;
      }
	  
	  public String valueOftimestamps(){
		     return timestamps ;
      } 
	  
	  public String valueOfprehash(){
		     return pre_hash ;
      }
	  
	  public String valueOfthishash(){
		     return this_hash ;
      }
}