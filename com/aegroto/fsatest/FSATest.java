package com.aegroto.fsatest;

import com.aegroto.fsatest.exceptions.FSATransitionFormatException;
import com.aegroto.fsatest.exceptions.FSAException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class FSATest extends Application{
    public static void main(String[] args) {
        launch(args);
    }
    
    public static void consolePrint(String str) {
        TEXTAREA_CONSOLE.appendText(str.concat("\n"));
        TEXTAREA_CONSOLE.positionCaret(TEXTAREA_CONSOLE.getText().length());
    }
    
    private BorderPane MAIN_BORDER;
    private GridPane CENTER_GRID;
    private Scene SCENE;
    
    private MenuBar TOP_MENU;
    private Menu MENU_IMPORT,MENU_EXPORT;
    
    private Button BUTTON_UPDATE,BUTTON_EXECUTE;
    
    private TextField TEXTFIELD_INPUT,TEXTFIELD_DEFINITION;
    private TextArea TEXTAREA_TRANSITIONS;
    private static TextArea TEXTAREA_CONSOLE;
    private Text TEXT_INPUT,TEXT_DEFINITION,TEXT_TRANSITIONS;
    
    private FSA currentFsa;
    
    @Override
    public void start(Stage stage) throws Exception {
            /**********/
            /***INIT***/
            /**********/
            stage.setTitle("Finite State Automata Test");
            
            MAIN_BORDER=new BorderPane();
            
            /*****************/
            /***TOP BUTTONS***/
            /*****************/
            FileChooser fileChooser=new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("Text Files","*.txt"),
                    new ExtensionFilter("All files","*.*"));
                
            TOP_MENU=new MenuBar();
            
            MENU_IMPORT=new Menu("Import");
            MenuItem mi_importFSA=new MenuItem("Import FSA from file");
            mi_importFSA.setOnAction((ActionEvent e) -> {
                fileChooser.setTitle("Import FSA");
                File file=fileChooser.showOpenDialog(stage);
                try {
                    if(file!=null) {
                        String[] fileParts=new String(
                            Files.readAllBytes(Paths.get(file.getAbsolutePath())), Charset.defaultCharset()
                        ).split("\n###\n");
                        TEXTFIELD_DEFINITION.setText(fileParts[0]);
                        TEXTAREA_TRANSITIONS.setText(fileParts[1]);
                        consolePrint("Successfully imported FSA from"+file.getPath());
                        try { updateFSAFromForm(); } catch(FSAException fsae) { consolePrint(fsae.getMessage()); }
                    }
                } catch(IOException ex) { consolePrint("Unable to import FSA from"+file.getPath()); }
            });
            
            MENU_IMPORT.getItems().addAll(mi_importFSA);   
            
            MENU_EXPORT=new Menu("Export");
            MenuItem mi_exportFSA=new MenuItem("Export FSA to file");
            mi_exportFSA.setOnAction((ActionEvent e) -> {
                fileChooser.setTitle("Import FSA");
                File file=fileChooser.showSaveDialog(stage);
                
                try (FileWriter fileWriter = new FileWriter(file)) {
                    fileWriter.write(TEXTFIELD_DEFINITION.getText().concat("\n###\n").concat(TEXTAREA_TRANSITIONS.getText())); 
                } catch(Exception ex) { consolePrint("Unable to export FSA from"+file.getPath()); }
            });
            
            MENU_EXPORT.getItems().addAll(mi_exportFSA);   
            
            TOP_MENU.getMenus().addAll(MENU_IMPORT,MENU_EXPORT);

            /*******************/
            /***CENTER INPUTS***/
            /*******************/
            CENTER_GRID=new GridPane();
            CENTER_GRID.setPadding(new Insets(10,10,10,10));
            CENTER_GRID.setHgap(10);
            CENTER_GRID.setVgap(10);
            
            //INPUT
            TEXT_INPUT=new Text("Input string");
            TEXTFIELD_INPUT=new TextField();
            
            CENTER_GRID.add(TEXT_INPUT, 0, 1);
            CENTER_GRID.add(TEXTFIELD_INPUT,1,1);
            
            //DEFINITION
            TEXT_DEFINITION=new Text("FSA Definition");
            TEXTFIELD_DEFINITION=new TextField();
            TEXTFIELD_DEFINITION.setText("<{a,b},{q0,q1},d,q0,{q1}>");
            
            CENTER_GRID.add(TEXT_DEFINITION, 0, 2);
            CENTER_GRID.add(TEXTFIELD_DEFINITION,1,2);
            
            //TRANSITIONS
            TEXT_TRANSITIONS=new Text("Transitions");
            TEXTAREA_TRANSITIONS=new TextArea();
            TEXTAREA_TRANSITIONS.setPrefHeight(100);
            TEXTAREA_TRANSITIONS.setText("d(q0,a)=q0;\nd(q0,b)=q1;\nd(q1,a)=q1;\nd(q1,b)=q1;");
            
            CENTER_GRID.add(TEXT_TRANSITIONS,0,3);
            CENTER_GRID.add(TEXTAREA_TRANSITIONS,1,3);
            
            
            BUTTON_UPDATE=new Button("Update FSA");
            BUTTON_UPDATE.setOnAction((ActionEvent e) -> {
                try {
                    updateFSAFromForm();
                } catch(FSAException fsae) { consolePrint(fsae.getMessage()); }
            });
                        
            BUTTON_EXECUTE=new Button("Execute");
            BUTTON_EXECUTE.setOnAction((ActionEvent e) -> {
                if(currentFsa!=null) {
                    String input=TEXTFIELD_INPUT.getText();
                    consolePrint("Elaborating '"+input+"'");
                    consolePrint("FSA returned:"+currentFsa.elaborateInput(input));
                } else consolePrint("ERROR:FSA is null,update it first");
            });
            
            CENTER_GRID.add(BUTTON_UPDATE,1,4);
            CENTER_GRID.add(BUTTON_EXECUTE,1,4);
            GridPane.setHalignment(BUTTON_EXECUTE, HPos.RIGHT);
            GridPane.setHalignment(BUTTON_UPDATE,HPos.LEFT);
            /*************/
            /***CONSOLE***/
            /*************/            
            TEXTAREA_CONSOLE=new TextArea();
            TEXTAREA_CONSOLE.setEditable(false);            
            /***************/
            /***START GUI***/ 
            /***************/
            MAIN_BORDER.setTop(TOP_MENU);
            MAIN_BORDER.setCenter(CENTER_GRID);
            MAIN_BORDER.setBottom(TEXTAREA_CONSOLE);
            
            SCENE=new Scene(MAIN_BORDER,425,450);
            stage.setScene(SCENE);
            stage.show();
            
            consolePrint("Welcome! Finite State Automata Test - aegroto - 2016\nInsert input,define the FSA and the press 'Execute' to begin");
    }
    
    protected FSA generateFSA(String definition,String transitions) throws FSAException {
        FSA fsa=null;
        if(definition.charAt(0)=='<'&&definition.charAt(definition.length()-1)=='>') {
            definition=definition.substring(1,definition.length()-1);
            String[] definition_s;
            definition_s=definition.split("\\{|\\}");
            

            definition_s[4]=definition_s[4].substring(1,definition_s[4].length()-1);
            String[] def_s_4_s=definition_s[4].split(",");
            definition_s[2]=def_s_4_s[0];
            definition_s[0]=def_s_4_s[1];
            
            definition_s[4]=definition_s[5];
            definition_s[5]=null;
            
            //for(int i=0;i<definition_s.length;i++) consolePrint("Definition["+i+"]:"+definition_s[i]);
            
            fsa=new FSA(definition_s[1],definition_s[3],definition_s[2],definition_s[0],definition_s[4]);
            
            /*if(!(definition_s[0].charAt(0)=='{'&&definition_s[0].charAt(definition_s[0].length()-1)=='}'))
                throw new FSADefinitionFormatException("Symbols must be included in { }");
            else definition_s[0]=definition_s[0].substring(1,definition_s[0].length()-2);
            fsa=new FSA(definition_s[0],
                        definition_s[1],
                        definition_s[2],
                        definition_s[3],
                        definition_s[4]);
            consolePrint("Valid definition registered");*/
        } else throw new FSATransitionFormatException("Definition must starts with '<' and finish with '>'");   
        
        fsa.setTransitions(transitions);
        
        //consolePrint(fsa.toString());
        consolePrint("Updated FSA");
        return fsa;
    }
    
    private void updateFSAFromForm() throws FSAException{
        currentFsa=generateFSA(
            TEXTFIELD_DEFINITION.getText(),
            TEXTAREA_TRANSITIONS.getText());
    }
}
