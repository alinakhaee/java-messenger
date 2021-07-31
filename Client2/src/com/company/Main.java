package com.company;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Main extends Application {
    final static int ServerPort = 1234;
    String receiver = "woody", sender;
    int lastY = -30, leftPaneY=43-16;
    Pane topPane = new Pane();
    final Color RED = Color.rgb(149, 69, 82), GRAY = Color.rgb(77, 77, 77),  WHITE = Color.rgb(203, 202, 198);
    @Override
    public void start(Stage primaryStage) throws Exception {
        InetAddress ip = InetAddress.getByName("localhost");
        Socket s = new Socket(ip, ServerPort);
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        //sign up scene
        Pane signUpPane = new Pane();
        TextField usernameField = new TextField(); usernameField.setLayoutX(226); usernameField.setLayoutY(157); usernameField.setPrefSize(150, 25);
        PasswordField passwordField = new PasswordField(); passwordField.setLayoutX(226); passwordField.setLayoutY(200); passwordField.setPrefSize(150, 25);
        passwordField.setPromptText("Password"); usernameField.setPromptText("Username");
        Button signUpButton1 = new Button("Sign Up"); signUpButton1.setLayoutX(271); signUpButton1.setLayoutY(235);
        signUpButton1.setOnMouseClicked(event -> {
            try{ dos.writeUTF("SignUp#" + usernameField.getText() + "#" + passwordField.getText());usernameField.clear(); passwordField.clear(); }
            catch (IOException e){e.printStackTrace();}
        });
        signUpPane.getChildren().addAll(usernameField, passwordField, signUpButton1);
        signUpPane.setStyle("-fx-background-color: rgb(149, 69, 82)");
        Scene signUpScene = new Scene(signUpPane, 600, 400);
        //log in scene
        Pane leftPane = new Pane();
        Pane logInPane = new Pane();
        logInPane.setStyle("-fx-background-color: rgb(82, 69, 149)");
        TextField usernameField1 = new TextField(); usernameField1.setLayoutX(226); usernameField1.setLayoutY(157); usernameField1.setPrefSize(150, 25);
        PasswordField passwordField1 = new PasswordField(); passwordField1.setLayoutX(226); passwordField1.setLayoutY(200); passwordField1.setPrefSize(150, 25);
        passwordField1.setPromptText("Password"); usernameField1.setPromptText("Username");
        Button logInButton = new Button("Log In"); logInButton.setLayoutX(275); logInButton.setLayoutY(235);
        Text signUpText = new Text(225, 331, "Don't Have An Account Yet?"); signUpText.setFill(Color.WHITE);
        Button signUpButton = new Button("Sign Up"); signUpButton.setLayoutX(271); signUpButton.setLayoutY(342);
        signUpButton.setOnMouseClicked(event -> primaryStage.setScene(signUpScene));
        logInPane.getChildren().addAll(usernameField1, passwordField1, logInButton, signUpText, signUpButton);
        Scene logInScene = new Scene(logInPane, 600, 400);

        ArrayList<Text> chats = new ArrayList<>();
        Circle blackCircle = new Circle(5, Color.BLACK); blackCircle.setLayoutX(68);
        BorderPane mainPane = new BorderPane();
        BorderPane chatBorderPane = new BorderPane();
        Pane pane = new Pane();
        pane.setMinSize(524, 300);
        pane.setStyle("-fx-background-color: silver");
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(pane);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefSize(526, 302);
        Pane downPane = new Pane();
        Button searchButton = new Button("Search");
        downPane.setStyle("-fx-background-color: silver");
        downPane.setPrefSize(600, 33);
        MenuBar menuBar = new MenuBar();
        menuBar.setPrefHeight(25); menuBar.setStyle("-fx-background-color: gold");
        Menu optionsMenu = new Menu("Options");
        MenuItem logOutItem = new MenuItem("Logout");
        MenuItem searchIDItem = new MenuItem("Search");
        logOutItem.setOnAction(event -> { try{dos.writeUTF("LogOut#");} catch (IOException e){e.printStackTrace();} });
        optionsMenu.getItems().addAll(logOutItem, searchIDItem);
        menuBar.getMenus().add(optionsMenu);
        leftPane.setStyle("-fx-background-color: white");
        leftPane.setPrefSize(74, 347);
        TextField newChatTextField = new TextField();
        newChatTextField.setLayoutY(200); newChatTextField.setLayoutX(200);
        Button newChat = new Button( "New Chat");
        newChat.setPrefSize(69, 25); newChat.setLayoutX(2); newChat.setLayoutY(2); newChat.setStyle("-fx-background-color: gold");
        newChat.setOnMouseClicked(event -> {
            pane.getChildren().clear();
            Button submit = new Button("Submit");
            pane.getChildren().addAll(newChatTextField, submit);
            submit.setOnMouseClicked(event1 -> {
                Text text = new Text(newChatTextField.getText());
                text.setY(leftPaneY+=16); text.setX(5);
                defineChatActivity(text, leftPane, pane, dos);
                chats.add(text);
            });
        });
        searchButton.setOnMouseClicked(event -> {
            try {
                pane.getChildren().clear();
                dos.writeUTF("search#" + sender + "#" + newChatTextField.getText());
            }
            catch(Exception e){e.printStackTrace();}
        });
        logInButton.setOnMouseClicked(event -> {
            try{
                dos.writeUTF("LogIn#" + usernameField1.getText() + "#" + passwordField1.getText());
                leftPane.getChildren().clear();
                leftPane.getChildren().add(newChat);
                leftPaneY = 43 - 16;
            }
            catch (IOException e) {e.printStackTrace();}
        });
        Rectangle rectangle = new Rectangle(526, 40, Color.GOLD);
        topPane.getChildren().add(rectangle);
        chatBorderPane.setCenter(scrollPane);
        chatBorderPane.setBottom(downPane);
        chatBorderPane.setTop(topPane);
        leftPane.getChildren().add(newChat);
        mainPane.setTop(menuBar);
        mainPane.setLeft(leftPane);
        mainPane.setCenter(chatBorderPane);
        Scene chatScene = new Scene(mainPane, 600, 400);
        Thread readMessage = new Thread(new Runnable() {@Override public void run() {
                while (true)
                    try {
                        String received = dis.readUTF();
                        System.out.println("received : " + received);
                        StringTokenizer st = new StringTokenizer(received,"#");
                        String command = st.nextToken();
                        if(command.equals("simpleChat")) {
                            String currentSender = st.nextToken(), messageReceived = st.nextToken();
                            boolean newChat = true;
                            for(Text texts : chats)
                                if(texts.getText().equals(currentSender))
                                    newChat = false;
                            if(newChat) {
                                Platform.runLater(new Runnable() {@Override public void run() {
                                        Text text = new Text(currentSender);
                                        text.setX(5); text.setY(leftPaneY+=16);
                                        defineChatActivity(text, leftPane, pane, dos);
                                        leftPane.getChildren().add(text);
                                        chats.add(text);
                                    }
                                });
                            }
                            else{
                                if(currentSender.equals(receiver)){
                                    Message msk = new Message(messageReceived);
                                    msk.setFill(RED);
                                    Platform.runLater(new Runnable() {@Override public void run() {
                                        msk.addToLayout(pane, 10, lastY+=30, true);
                                        scrollPane.vvalueProperty().bind(pane.heightProperty());
                                    }});
                                    System.out.println(messageReceived);
                                }
                                else
                                    Platform.runLater(new Runnable() {@Override public void run() { getByUsername(currentSender, chats).setFill(Color.GREENYELLOW); }});
                            }
                        }
                        else if(command.equals("PreviousMessages")){
                            while (st.hasMoreElements()){
                                String temp = st.nextToken(), sender = st.nextToken(), receivertemp = st.nextToken(), message = st.nextToken(), time = st.nextToken();
                                Message msk = new Message(message);
                                if(temp.equals("pvChat")) {
                                    if (sender.equals(receiver)) {
                                        msk.setFill(RED);
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                msk.addToLayout(pane, 10, lastY += 30, true, time);
                                                scrollPane.vvalueProperty().bind(pane.heightProperty());
                                            }
                                        });
                                    }
                                    else {
                                        msk.setFill(GRAY);
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                msk.addToLayout(pane, 525 - msk.getWidth() - 15, lastY += 30, false, time);
                                                scrollPane.vvalueProperty().bind(pane.heightProperty());
                                            }
                                        });
                                    }
                                }
                                else if(temp.equals("file")){
                                    FileMessage fileMessage = new FileMessage(message);
                                    if(sender.equals(receiver)) {
                                        fileMessage.setFill(RED);
                                        fileMessage.setOnKeyPressed();
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                fileMessage.addtoLayout(pane, 10, lastY += 30, true, time);
                                            }
                                        });
                                    }
                                    else{
                                        fileMessage.setFill(GRAY);
                                        fileMessage.setOnKeyPressed();
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                fileMessage.addtoLayout(pane, 525-fileMessage.getWidth()-15, lastY+=30, false, time);
                                            }
                                        });
                                    }

                                }
                            }
                        }
                        else if(command.equals("previousChatActivity")){
                            String[] users = received.split("#");
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    for(int i=1 ; i<users.length ; i++) {
                                        Text text = new Text(users[i]);
                                        text.setY(leftPaneY+=16);
                                        text.setX(5);
                                        defineChatActivity(text, leftPane, pane, dos);
                                    }
                                }
                            });
                        }
                        else if(command.equals("file")){
                            String senderUsername = st.nextToken(), receiverUsername = st.nextToken(), fileName = st.nextToken(), lengthString = st.nextToken();
                            FileOutputStream fileOutputStream = new FileOutputStream("Data\\" + fileName);
                            byte[] bytes = new byte[(int)Long.parseLong(lengthString)];
                            dis.read(bytes, 0, bytes.length);
                            fileOutputStream.write(bytes, 0, bytes.length);
                            fileOutputStream.close();
                            FileMessage fileMessage = new FileMessage(fileName);
                            fileMessage.setOnKeyPressed();
                            Platform.runLater(new Runnable() {@Override public void run() {
                                fileMessage.setFill(RED);
                                fileMessage.addtoLayout(pane, 10, lastY+=30, true);
                            }});
                        }
                        else if(command.equals("searched")){
                            String string = received.substring(9, received.length()-1);
                            String[] messages = string.split("%");
                            for(int i=0 ; i<messages.length ; i++)
                                System.out.println(messages[i]);
                        }
                        else if(command.equals("UsernameNotFound"))
                            Platform.runLater(new Runnable() {@Override public void run() { logInPane.getChildren().add(new Text(250, 250, "USERNAME NOT FOUND")); }});
                        else if(command.equals("SuccessfulLogIn"))
                            Platform.runLater(new Runnable() {@Override public void run() {
                                sender = usernameField1.getText();
                                passwordField1.clear();
                                usernameField1.clear();
                                try{dos.writeUTF("getChatActivity");}
                                catch(Exception e){e.printStackTrace();}
                                primaryStage.setScene(chatScene);
                            }});
                        else if(command.equals("SuccessfulSignUp"))
                            Platform.runLater(new Runnable() {@Override public void run() { primaryStage.setScene(logInScene); }});
                        else if(command.equals("SuccessfulLogOut"))
                            Platform.runLater(new Runnable() {@Override public void run() {
                                logInPane.getChildren().clear();
                                logInPane.getChildren().addAll(usernameField1, passwordField1, logInButton, signUpButton, signUpText);
                                primaryStage.setScene(logInScene);
                            }});
                    }
                    catch (Exception e) { e.printStackTrace(); }
            }
        });
        readMessage.start();

        TextField textField = new TextField();
//        searchIDItem.setOnAction(event -> {
//            pane.getChildren().clear();
//            pane.getChildren().addAll(newChatTextField, searchButton);
//        });
        textField.setPrefSize(400, 26); textField.setLayoutX(2); textField.setLayoutY(3);
        Button send = new Button("Send");
        send.setPrefSize(55, 25); send.setTextFill(Color.BLACK); send.setStyle("-fx-background-color: gold"); send.setLayoutX(405); send.setLayoutY(3);
        send.setOnMouseClicked(event -> sendMessage(textField, pane, dos) );
        Button sendFile = new Button("Send File");
        sendFile.setTextFill(Color.BLACK); sendFile.setStyle("-fx-background-color: gold"); sendFile.setLayoutX(462); sendFile.setLayoutY(3);
        sendFile.setOnMouseClicked(event -> {
            try {
                FileChooser fileChooser = new FileChooser();
                File selectedFile = fileChooser.showOpenDialog(primaryStage);
                dos.writeUTF("file#" + selectedFile.getName() + "#" + receiver + "#" + Message.getCurrentTime() + "#" + selectedFile.length());
                byte[] bytes = new byte[(int) selectedFile.length()];
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                fileInputStream.read(bytes, 0, bytes.length);
                dos.write(bytes, 0, bytes.length);
                FileMessage fileMessage = new FileMessage(selectedFile.getName());
                fileMessage.setFill(GRAY);
                fileMessage.setOnKeyPressed();
                fileMessage.addtoLayout(pane, 525-fileMessage.getWidth()-15, lastY+=30, false);
            }
            catch(Exception e){e.printStackTrace();}
        });
        textField.setOnKeyPressed(keyevent -> { if(keyevent.getCode().equals(KeyCode.ENTER)){ sendMessage(textField, pane, dos); scrollPane.vvalueProperty().bind(pane.heightProperty()); } });
        downPane.getChildren().addAll(textField, send, sendFile);
        downPane.setStyle("-fx-background-color: silver");
        primaryStage.setScene(logInScene);
        primaryStage.show();
    }
    public static void main(String args[]) { launch(args); }
    public void sendMessage(TextField textField, Pane pane, DataOutputStream dos){
        String message = textField.getText();
        textField.clear();
        Message message1 = new Message(message);
        message1.setFill(GRAY);
        message1.addToLayout(pane, 525-message1.getWidth()-15, lastY+=30, false);
        try{ dos.writeUTF("simpleChat#" + message + "#" + receiver + "#" + message1.getTime());}
        catch (IOException e){ e.printStackTrace();}
    }
    public Text getByUsername(String username, ArrayList<Text> chats){
        for(Text text : chats)
            if(text.getText().equals(username))
                return text;
        return null;
    }
    public void defineChatActivity(Text text, Pane leftPane, Pane pane, DataOutputStream dos){
        Circle blackCircle = new Circle( 5, Color.BLACK);
        blackCircle.setLayoutX(68);
        leftPane.getChildren().add(text);
        text.setOnMouseEntered(event2 -> {blackCircle.setLayoutY(text.getY()-4); leftPane.getChildren().add(blackCircle);});
        text.setOnMouseExited(event2 -> leftPane.getChildren().remove(blackCircle));
        text.setOnMouseClicked(event2 -> {
            text.setFill(Color.BLACK);
            lastY=-30;
            Text userText = new Text(text.getText());
            userText.setFont(Font.font(25));
            userText.setY(32); userText.setX(5);
            topPane.getChildren().clear();
            Rectangle rectangle = new Rectangle(526, 40, Color.GOLD);
            topPane.getChildren().add(rectangle);
            topPane.getChildren().add(userText);
            receiver = text.getText();
            pane.getChildren().clear();
            try{dos.writeUTF("getPreviousMessages#"+sender+"#"+receiver);}
            catch(Exception e){ e.printStackTrace();}
        });
    }
}

class Message{
    String message, time;
    Text text;
    Rectangle rectangle;
    Message(String message){
        this.message = message;
        text = new Text(message);
        text.setFont(Font.font(15));
        rectangle = new Rectangle(text.getLayoutBounds().getWidth()+10 , 30);
    }
    public double getWidth(){return rectangle.getWidth();}
    public void addToLayout(Pane pane, double x, double y, boolean isReceiver, String time){
        text.setX(x+5); text.setY(y+21); text.setFill(Color.WHITE);
        rectangle.setX(x); rectangle.setY(y);
        Text timetext;
        if(isReceiver)
            timetext = new Text(x + this.getWidth() + 5, y+21, time);
        else
            timetext = new Text(x - 30, y+21, time);
        timetext.setFont(Font.font(9));
        pane.getChildren().addAll(rectangle, text, timetext);
    }
    public void addToLayout(Pane pane, double x, double y, boolean isReceiver){
        text.setX(x+5); text.setY(y+21); text.setFill(Color.WHITE);
        rectangle.setX(x); rectangle.setY(y);
        Text time;
        if(isReceiver)
            time = new Text(x + this.getWidth() + 5, y+21, this.getTime());
        else
            time = new Text(x - 30, y+21, this.getTime());
        time.setFont(Font.font(9));
        pane.getChildren().addAll(rectangle, text, time);
    }
    public String getTime(){
        LocalTime time = LocalTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");
        this.time = format.format(time);
        return this.time;
    }
    public static String getCurrentTime(){
        LocalTime time = LocalTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");
        return format.format(time);
    }
    public void setFill(Color color){ rectangle.setFill(color);}
}

class FileMessage{
    String fileName;
    Text text;
    Rectangle rectangle ;
    ImageView fileView = new ImageView(new Image(new File("file.png").toURI().toString()));
    public FileMessage(String fileName) {
        this.fileName = fileName;
        text = new Text(fileName);
        rectangle = new Rectangle(text.getLayoutBounds().getWidth()+40 , 30);
    }
    public void addtoLayout(Pane pane, double x, double y, boolean isReceiver, String time){
        rectangle.setX(x); rectangle.setY(y);
        Text timeText;
        if(isReceiver)
            timeText = new Text(x + rectangle.getWidth() + 5, y+21, time);
        else
            timeText = new Text(x - 30, y+21, time);
        timeText.setFont(Font.font(9));
        fileView.setFitHeight(29); fileView.setFitWidth(29); fileView.setY(y); fileView.setX(x);
        text.setX(x+31); text.setY(y+21); text.setFill(Color.WHITE);
        pane.getChildren().addAll(rectangle,fileView, text, timeText);

    }
    public void addtoLayout(Pane pane, double x, double y, boolean isReceiver){
        rectangle.setX(x); rectangle.setY(y);
        Text time;
        if(isReceiver)
            time = new Text(x + rectangle.getWidth() + 5, y+21, Message.getCurrentTime());
        else
            time = new Text(x - 30, y+21, Message.getCurrentTime());
        time.setFont(Font.font(9));
        fileView.setFitHeight(29); fileView.setFitWidth(29); fileView.setY(y); fileView.setX(x);
        text.setX(x+31); text.setY(y+21); text.setFill(Color.WHITE);
        pane.getChildren().addAll(rectangle,fileView, text, time);
    }
    public void setFill(Color color){ rectangle.setFill(color);}
    public double getWidth(){return rectangle.getWidth();}
    public void setOnKeyPressed(){
        rectangle.setOnMouseClicked(event -> { try{
            File file = new File("../Data/" +fileName);
            if(Desktop.isDesktopSupported())
                Desktop.getDesktop().open(file);
        } catch (IOException e){e.printStackTrace();} });
        text.setOnMouseClicked(event -> { try{
            File file = new File("../Data/" + fileName);
            if(Desktop.isDesktopSupported())
                Desktop.getDesktop().open(file);
        } catch (IOException e){e.printStackTrace();} });
        fileView.setOnMouseClicked(event -> { try{
            File file = new File("../Data/"+fileName);
            if(Desktop.isDesktopSupported())
                Desktop.getDesktop().open(file);
        } catch (IOException e){e.printStackTrace();} });
    }
}