/*
Program Name: Loan Amortization Calculator
Program Purpose: Calculate the monthly payment, total payment, and output a payment schedule for
   a specific loan amount, payment duration, and annual interest rate.
Author: Michael Girard
Date Last Modified: June 04, 2014
Program created using the comprehensive 10th edition textbook, JDK 6u7, and jGRASP 2.0.0_11.
*/

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JDialog;

public class AutoPowerSaver implements Runnable{
   private static final String JAVA_ICON = "Java_Icon.png";
   private static final int SECONDS_IN_A_MINUTE = 60;
   private static final int GUID_LENGTH = 36;
   private static int minutesToWait = 20;
   private static String POWER_SAVING_GUID; 
   private static String HIGH_PERFORMANCE_GUID;
   private static Runtime rt = Runtime.getRuntime();
   private int count = 0;
   private boolean highPerformance;
   private Point lastMouseLocation;
   private Point currentMouseLocation;
   
   public AutoPowerSaver(){
      lastMouseLocation = MouseInfo.getPointerInfo().getLocation();
   }
   
   public static void main(String[] args){
      getGUIDs();
      writeBatch(new File("RunOnIdle.bat"), POWER_SAVING_GUID);
      writeBatch(new File("RunOnActive.bat"), HIGH_PERFORMANCE_GUID);
      
      new Thread(new AutoPowerSaver()).start();
   }
   
   public static void getGUIDs(){
      try{
         java.util.Scanner GUIDOutput = new java.util.Scanner(rt.exec("cmd /c powercfg -list").getInputStream());
         StringBuilder sb = new StringBuilder();
         while(GUIDOutput.hasNext()){
            sb.append(GUIDOutput.next());
         }
         int index = sb.indexOf("(Pow");
         POWER_SAVING_GUID = sb.substring(index - GUID_LENGTH, index);
         index = sb.indexOf("(High");
         HIGH_PERFORMANCE_GUID = sb.substring(index - GUID_LENGTH, index);
         System.out.println(POWER_SAVING_GUID + "\n" + HIGH_PERFORMANCE_GUID);
      }
      catch(IOException e){
         e.printStackTrace();
      }   
   }
   
   public static void writeBatch(File file, String GUID){
      try{
         PrintWriter printer = new PrintWriter(file);
         printer.println("powercfg -setactive " + GUID);
         printer.print("exit");
         printer.close();
      }
      catch (FileNotFoundException e){
      
      }
   }
   
   public void run(){
      try{
         TrayIcon icon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemClassLoader().getResource(JAVA_ICON)));
         icon.setImageAutoSize(true);
         icon.setToolTip("AutoPowerSaver");
         
         
         JPopupMenu menu = new JPopupMenu();
         JMenuItem exitMenuItem = new JMenuItem("Exit");
         
         JMenu timeMenu = new JMenu("Switch Timing");
         JMenuItem fiveMinutes = new JMenuItem("5 Minutes");
         JMenuItem tenMinutes = new JMenuItem("10 Minutes");
         JMenuItem fifteenMinutes = new JMenuItem("15 Minutes");
         JMenuItem twentyMinutes = new JMenuItem("20 Minutes");
         JMenuItem thirtyMinutes = new JMenuItem("30 Minutes");
         JMenuItem sixtyMinutes = new JMenuItem("60 Minutes");
         timeMenu.add(sixtyMinutes);
         timeMenu.add(thirtyMinutes);
         timeMenu.add(twentyMinutes);
         timeMenu.add(fifteenMinutes);
         timeMenu.add(tenMinutes);
         timeMenu.add(fiveMinutes);
         
         menu.add(timeMenu);
         menu.add(new JSeparator());
         menu.add(exitMenuItem);
         
         JDialog dialog = new JDialog();
         
         exitMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
               System.exit(0);
            }
         });
         
         fiveMinutes.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
               minutesToWait = 5;
               System.out.println("Time changed to 5");
            }
         });
         
         tenMinutes.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
               minutesToWait = 10;
               System.out.println("Time changed to 10");
            }
         });
         
         fifteenMinutes.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
               minutesToWait = 15;
               System.out.println("Time changed to 15");
            }
         });
         
         twentyMinutes.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
               minutesToWait = 20;
               System.out.println("Time changed to 20");
            }
         });
         
         thirtyMinutes.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
               minutesToWait = 30;
               System.out.println("Time changed to 30");
            }
         });
         
         sixtyMinutes.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
               minutesToWait = 60;
               System.out.println("Time changed to 60");
            }
         });
         
         timeMenu.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){
               timeMenu.setPopupMenuVisible(true);
            }
         });
         
         icon.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e){
               if (e.getButton() == MouseEvent.BUTTON3){
                  menu.setLocation(e.getX(), e.getY());
                  menu.setInvoker(menu);
                  menu.setVisible(true);
                  dialog.setVisible(true);
               }
            }
         });
         
         dialog.setSize(10,10);
         dialog.addWindowFocusListener(new WindowFocusListener(){
            public void windowLostFocus(WindowEvent e){
               dialog.setVisible(false);
               menu.setVisible(false);
            }
            public void windowGainedFocus (WindowEvent e){}
         });
         
         SystemTray.getSystemTray().add(icon);
      }
      catch (AWTException ex){
         JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
      }
      
      
      
      
      while(true){
         try{
            Thread.sleep(1000);
         }
         catch (InterruptedException ex){
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
         }
         currentMouseLocation = MouseInfo.getPointerInfo().getLocation();
         if (currentMouseLocation.equals(lastMouseLocation)){
            count++;
            //System.out.println(count + " of " + minutesToWait * SECONDS_IN_A_MINUTE);
            if(count >= (minutesToWait * SECONDS_IN_A_MINUTE) && highPerformance){
               try{
                  //System.out.println("Idle Now");
                  rt.exec("cmd /c start /B RunOnIdle.bat");
                  highPerformance = false;
               }
               catch (IOException ex2){
                  JOptionPane.showMessageDialog(null, "Error: " + ex2.getMessage());
               }
            }
         }
         else{
            if(!highPerformance){
               try{
                  rt.exec("cmd /c start /B RunOnActive.bat");
                  highPerformance = true;
               }
               catch (IOException ex2){
                  JOptionPane.showMessageDialog(null, "Error: " + ex2.getMessage());
               }
            }
            count = 0;
            lastMouseLocation = currentMouseLocation;
            
         }
      }
   }
}