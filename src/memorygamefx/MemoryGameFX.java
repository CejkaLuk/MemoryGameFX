package memorygamefx;

import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import javafx.scene.image.Image;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.scene.control.Alert; // To show a message box, easy way
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.SeparatorMenuItem;

import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MemoryGameFX extends Application 
{
    private int ROWS = 4;
    private int COLS = 4;

    // BorderPane to encapsulate everything in the main window.
    private final BorderPane border_pane = new BorderPane();
    
    // GridPane to encapsulate just the cards.
    private final GridPane grid = new GridPane();
    
    private final MenuBar menu_bar = new MenuBar();
    
    // Difficulty setting.
    private String difficulty;
    
    // Main windows width and height, to be able to use them with setting the size of border_pane and grid.
    private double stage_width;
    private double stage_height;
    
    // Deck of all cards.
    private CardDeck card_deck;
    
    // All image required (NOT USED, since elements of ImageStore are static. Netbeans "accessing static elements")
    //private ImageStore image_store;
    
    // Haven't figured out how to get around this. Need there variables in the onMouseClick. WHAT is the equivalent to emit/connect from Qt in Java??
    //////////////////////////////////////////////////////
    public static int num_players;
    public static int elapsed_steps = 0;
    
    public static int player_1_score;
    public static Label lbl_player_1_score;
    public static boolean player_1_active;
    
    public static int player_2_score;
    public static Label lbl_player_2_score;
    //////////////////////////////////////////////////////
    
    public void newGame()
    {
        // If there are some elapsed steps (a game is in progress).
        if ( elapsed_steps > 0 )
        {
            Alert alert = new Alert( AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO );
            alert.setTitle( "New Game?");
            alert.setHeaderText( "Are you sure you want to start a new game?" );
            alert.setContentText( "If click 'YES', you will lose all current progress." );
            alert.showAndWait();

            // The player(s) don't want to abandon the game, don't continue with newGame setup (return).
            if ( alert.getResult() == ButtonType.NO )
                return;
        }
        
        // Reset elapsed steps for newGame.
        elapsed_steps = 0;
        
        // If there is a difficulty setting, set appropriate rows and cols.
        if ( difficulty != null )
        switch ( difficulty ) 
        {
            case "EASY":
                ROWS = 2;
                COLS = 4;
                break;
            case "MEDIUM":
                ROWS = 3;
                COLS = 4;
                break;
            case "HARD":
                ROWS = 6;
                COLS = 6;
                break;
            default:
                break;
        }
        
        // Set up HBox (within a VBox, To make divide the top of the borderpane into the menu_bar and players_hbox - hosts Player scores).
        //      parameter of the constructor is the spacing between Nodes.
        VBox top_layout = new VBox(10);
        HBox players_hbox = new HBox(100);
        
            players_hbox.setAlignment( Pos.TOP_CENTER );

            // Reset player 1's score.
            player_1_score = 0;
            // Player 1 is starting first, thus he will be highlighted (larger text font && background color green) right away.
            lbl_player_1_score = new Label( " Player 1 Score = " + player_1_score + " " );
            lbl_player_1_score.setFont( new Font("Serif", 40) );
            lbl_player_1_score.setBackground( new Background(new BackgroundFill(Color.rgb(0, 255, 0), CornerRadii.EMPTY, Insets.EMPTY)) );

            // Reset player 1's score.
            player_2_score = 0;
            // Player 2 never starts first, thus, he doesn't need to be highlighted yet.
            lbl_player_2_score = new Label( " Player 2 Score = " + player_2_score + " " );
            lbl_player_2_score.setFont( new Font("Serif", 36) );

            players_hbox.getChildren().addAll( lbl_player_1_score, lbl_player_2_score );

            // Switch for number of players.
            switch( num_players )
            {
                // If there is 1 player.
                case 1:
                    player_1_active = true;
                    
                    // Hide player 2's score
                    lbl_player_2_score.setVisible( false );
                    break;
                    
                // If there are 2 players.
                case 2:
                    player_1_active = true;
                    
                    // Show player 2's score.
                    lbl_player_2_score.setVisible( true );
                    break;
            }

            top_layout.getChildren().addAll( menu_bar,
                                            players_hbox );
            
            // Set the top layout of the border_pane to be the menu_bar and players_hbox.
            border_pane.setTop( top_layout );

        // Set up grid (card area)
            grid.setAlignment( Pos.CENTER );
            grid.setGridLinesVisible( false );

            // Padding on the sides.
            grid.setPadding( new Insets(20, 10, 20, 10) );
            grid.setVgap( 5 );
            grid.setHgap( 5 );
            
        // Set up the cards
        ImageStore.card_back_image = new Image( "images/card_back.png" );
        ImageStore.card_face_images = new ArrayList<>();
        
        // Make the 0th element the back_image, won't be used, but skips issues with card_ID starting from 1 (instead of 0, since ArrayList indexes from 0).
        ImageStore.card_face_images.add( ImageStore.card_back_image );
        
        // Add all necessary cards (unique cards), only half of the total cards are needed.
        //  Since card_IDs are from 1, the unique number of cards is shifted 1.
        for ( int card_ID = 1; card_ID < (ROWS*COLS/2)+1; card_ID++ )
        {
            // Load the card image.
            String image_file_name = ( "images/" + card_ID + ".png" );
            
            // Set card the image to the face image of the card.
            Image card_face_image = new Image( image_file_name );

            // Add the face image of the card to the imageStore.
            ImageStore.card_face_images.add(card_face_image );
        }
        
        // Initialiaze the deck with ROWS*COLS/2 unique cards (it will have ROWS*COLS total cards).
        card_deck = new CardDeck( ROWS*COLS/2 );
        card_deck.shuffle();
        
        // Reset the grid of cards.
        grid.getChildren().clear();
        
        int card_index = 0;
        
        // For every card.
        for( int i = 0; i < ROWS; i++ )
        {
            for( int j = 0; j < COLS; j++ )
            {
                // Set the size fit in proportion with the stage size.
                card_deck.getCard( card_index ).setFitHeight( (stage_height-300)/ROWS );
                card_deck.getCard( card_index ).setFitWidth( (stage_width-100)/COLS );
                
                // Add the card to the grid.
                grid.add( card_deck.getCard(card_index), j, i );
                
                // Increment card_index to add next card.
                card_index++;
            }
        }
    }
    
    public void surrenderGame()
    {
        if ( elapsed_steps > 0 )
        {
            Alert alert = new Alert( AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO );
            alert.setTitle( "Surrender Game?");
            alert.setHeaderText( "Are you sure you want to surrender this game?" );
            alert.setContentText( "If click 'YES', you will lose all current progress." );
            alert.showAndWait();

            if ( alert.getResult() == ButtonType.NO )
            {
                return;
            }
        }
        elapsed_steps = 0;
        
        // Uncover all cards.
        for ( int card_index = 0; card_index < ROWS*COLS; card_index++ )
        {
            Card card = ( Card )grid.getChildren().get( card_index );
            if ( !card.getIsMatched() )
            {
                card.setFaceUp();
                card.setIsMatched(true);
            }
        }
    }
    
    public static void gameWon() // Source: https://stackoverflow.com/questions/11662857/javafx-2-1-messagebox
   {
        if ( num_players == 1 )
        {
            Platform.runLater(() -> 
            {
                Alert alert = new Alert( Alert.AlertType.INFORMATION, "", ButtonType.OK );
                alert.setTitle( "Game Won!" );
                alert.setHeaderText( "Congratulations, you have won!" );
                alert.setContentText( "It took you " + elapsed_steps + " steps to win." );
                alert.showAndWait();
                
                // elapsed_steps need to be reset after the OK button is hit, otherwise they will reset before the alert is displayed, thus erasing how many steps elapsed.
                if ( alert.getResult() == ButtonType.OK )
                    elapsed_steps = 0;
            });
        }
        else
        {
            if ( player_1_score > player_2_score )
            {
                Platform.runLater(() -> 
                {
                    Alert alert = new Alert( Alert.AlertType.INFORMATION, "", ButtonType.OK );
                    alert.setTitle( "Game Won!" );
                    alert.setHeaderText( "Congratulations Player 1, you have won!" );
                    alert.setContentText( "You beat Player 2 with a score of " + player_1_score + " to " + player_2_score + " in " + elapsed_steps + " steps!" );
                    alert.showAndWait();
                    
                    if ( alert.getResult() == ButtonType.OK )
                        elapsed_steps = 0;
                });
            }
            else if ( player_2_score > player_1_score )
            {
                Platform.runLater(() -> 
                {
                    Alert alert = new Alert( Alert.AlertType.INFORMATION, "", ButtonType.OK );
                    alert.setTitle( "Game Won!" );
                    alert.setHeaderText( "Congratulations Player 2, you have won!" );
                    alert.setContentText( "You beat Player 1 with a score of " + player_2_score + " to " + player_1_score + " in " + elapsed_steps + " steps!" );
                    alert.showAndWait();
                    
                    if ( alert.getResult() == ButtonType.OK )
                        elapsed_steps = 0;
                });
            }
            else
            {
                Platform.runLater(() -> 
                {
                    Alert alert = new Alert( Alert.AlertType.INFORMATION, "", ButtonType.OK );
                    alert.setTitle( "Game Over!" );
                    alert.setHeaderText( "It's a tie!" );
                    alert.setContentText( "No one won today, you both found " + player_1_score + " matches in " + elapsed_steps + " steps!" );
                    alert.showAndWait();
                    
                    if ( alert.getResult() == ButtonType.OK )
                        elapsed_steps = 0;
                });
            }
        }
    }
    
    // Change the active player (if card pair revealed by player didn't match).
    public static void activePlayerChange()
    {
        if ( num_players == 2 )
        {
            // Make Player 2 active
            if ( player_1_active )
            {
                player_1_active = false;

                // Highlight Player 2's score area, to indicate his turn.
                lbl_player_2_score.setFont( new Font("Serif", 40) );
                lbl_player_2_score.setBackground( new Background(new BackgroundFill(Color.rgb(0, 255, 0), CornerRadii.EMPTY, Insets.EMPTY)) );

                // Un-highlight Player 1's score area, to indicate it isn't his turn.
                lbl_player_1_score.setFont( new Font("Serif", 36) );
                lbl_player_1_score.setBackground( new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)) );
            }
            
            // Make Player 1 active
            else
            {
                player_1_active = true;

                // Highlight Player 1's score area, to indicate his turn.
                lbl_player_1_score.setFont( new Font("Serif", 40) );
                lbl_player_1_score.setBackground( new Background(new BackgroundFill(Color.rgb(0, 255, 0), CornerRadii.EMPTY, Insets.EMPTY)) );

                // Un-highlight Player 2's score area, to indicate it isn't his turn.
                lbl_player_2_score.setFont( new Font("Serif", 36) );
                lbl_player_2_score.setBackground( new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)) );
            }
        }
        // Doesn't matter who made the step, when it comes to 2 players, this stat is useless individually, since one player can see what the other revealed, giving him info, therefore steps.
        elapsed_steps += 1; 
    }
    
    // A match was made
    public static void playerScoreAdd()
    {
        if ( player_1_active )
        {
            player_1_score += 1;
            lbl_player_1_score.setText( " Player 1 Score = " + player_1_score + " " );
        }
        else
        {
            player_2_score += 1;
            lbl_player_2_score.setText( " Player 2 Score = " + player_2_score + " " );
        }
        
        // A step was taken.
        elapsed_steps += 1;
    }

    private void setupMenu( Stage stage )
    {
        Menu menu_file = new Menu( "File" );
            MenuItem menu_new_game = new MenuItem( "New Game" );
            MenuItem menu_surrender = new MenuItem( "Surrender" );
            MenuItem menu_close = new MenuItem( "Close" );

                menu_new_game.setOnAction( (ActionEvent e) -> { newGame(); });
                menu_surrender.setOnAction( (ActionEvent e) -> { surrenderGame(); });
                menu_close.setOnAction( (ActionEvent e) -> { stage.close(); });

        menu_file.getItems().addAll(menu_new_game,
                                    menu_surrender,
                                    new SeparatorMenuItem(),
                                    menu_close );

        Menu menu_options = new Menu( "Options" );
            CheckMenuItem menu_1_player = new CheckMenuItem( "1 Player" );
            CheckMenuItem menu_2_players = new CheckMenuItem( "2 Players" );
            Menu menu_difficulty = new Menu( "Difficulty" );
                CheckMenuItem menu_easy = new CheckMenuItem( "Easy" );
                CheckMenuItem menu_medium = new CheckMenuItem( "Medium" );
                CheckMenuItem menu_hard = new CheckMenuItem( "Hard" );

                menu_1_player.setOnAction( (ActionEvent e) ->
                {
                    num_players = 1;
                    menu_2_players.setSelected( false );
                    newGame();
                });
                menu_2_players.setOnAction( (ActionEvent e) ->
                {
                    num_players = 2;
                    menu_1_player.setSelected( false );
                    newGame();
                });

                menu_easy.setOnAction( (ActionEvent e) ->
                    {
                        difficulty = "EASY";
                        menu_medium.setSelected( false );
                        menu_hard.setSelected( false );
                        newGame();
                    });
                menu_medium.setOnAction( (ActionEvent e) ->
                    {
                        difficulty = "MEDIUM";
                        menu_easy.setSelected( false );
                        menu_hard.setSelected( false );
                        newGame();
                    });
                menu_hard.setOnAction( (ActionEvent e) ->
                    {
                        difficulty = "HARD";
                        menu_easy.setSelected( false );
                        menu_medium.setSelected( false );
                        newGame();
                    });

            menu_difficulty.getItems().addAll( menu_easy,
                                               menu_medium,
                                               menu_hard );

        menu_options.getItems().addAll( menu_1_player,
                                        menu_2_players,
                                        new SeparatorMenuItem(),
                                        menu_difficulty );

        Menu menu_help = new Menu( "Help" );
            MenuItem menu_instructions = new MenuItem( "Instructions" );
            MenuItem menu_about = new MenuItem( "About" );
            
            menu_instructions.setOnAction( (ActionEvent e) ->
            {
                Alert alert = new Alert( Alert.AlertType.INFORMATION );
                alert.setTitle( "Instructions" );
                alert.setHeaderText( "How to play:" );
                alert.setContentText( "By default the Number of Players is 1 and the game is set to Easy.\n"
                                    + "\nGameplay (1 Player):".toUpperCase()
                                    + "  1. Click two cards to reveal them, if they match, they'll remain revealed,\n"
                                    + "     if they don't match, they will get flipped face down when the the next card to reveal is clicked."
                                    + "  2. Continue this untill all cards are matched.\n"
                                    + "\nGameplay (2 Players):".toUpperCase()
                                    + "  1. Same gameplay mechanics as for one player, but, when a player has a successful\n"
                                    + "     match of cards, they get to play until they reveal a non-matching pair of cards,\n"
                                    + "     then the other player begins to play, until they reveal a non-matching pair, and so on\n."
                                    + "\nOptions:\n".toUpperCase()
                                    + "  - 1 or 2 Players\n"
                                    + "  - Easy, Medium and Hard difficulties." );
                alert.showAndWait();
            });
            menu_about.setOnAction( (ActionEvent e) ->
            {
                Alert alert = new Alert( Alert.AlertType.INFORMATION );
                alert.setTitle( "About" );
                alert.setHeaderText( "This project was created by Lukas Cejka\n"
                                    + "for the Programming in JAVA course at \n"
                                    + "CTU FNSPE in the Winter Semestr of 2018/2019." );
                alert.setContentText( "For any information or questions, please contact lukas(dot)ostatek(at)gmail(dot)com" );
                alert.showAndWait();
            });
            
        menu_help.getItems().addAll( menu_instructions,
                                     menu_about );

        menu_bar.getMenus().addAll(menu_file,
                                   menu_options,
                                   menu_help );

        // SET DEFAULT START OPTIONS
        menu_easy.setSelected( true );
        menu_1_player.setSelected( true );
        difficulty = "EASY";
        num_players = 1;
    }
    
    private void setupWindow( Stage stage )
    {
        stage.setTitle( "Memory Game" );
        stage.setWidth( 1000 );
        stage.setHeight( 1000 );
        
        stage_width = stage.getWidth();
        stage_height = stage.getHeight();
        
        border_pane.setPrefSize( stage.getWidth(), stage.getHeight() );
        border_pane.setBackground( new Background(new BackgroundFill(Color.rgb(220, 220, 220), CornerRadii.EMPTY, Insets.EMPTY)) );
        
        border_pane.setPadding( new Insets(0, 0, 0, 0) );
    }
    
    private void setupButtons( Stage stage )
    {
        // Encapsulate all buttons in a HBox, that will then be added into the Bottom of BorderPane.
        HBox btns_hbox = new HBox( 30 );

            btns_hbox.setAlignment( Pos.CENTER );
            btns_hbox.setPadding( new Insets(0, 0, 20, 0) );

            Button new_Game_btn = new Button( "New Game" );
            new_Game_btn.setFont( new Font("Serif", 20) );

            Button surrender_btn = new Button( "Surrender" );
            surrender_btn.setFont( new Font("Serif", 20) );

            Button quit_btn = new Button( "Quit" );
            quit_btn.setFont( new Font("Serif", 20) );

        // Set up button actions
        new_Game_btn.setOnAction( (ActionEvent e) -> { newGame(); });
        surrender_btn.setOnAction( (ActionEvent e) -> { surrenderGame(); });
        quit_btn.setOnAction( (ActionEvent e) -> { stage.close(); });

        btns_hbox.getChildren().addAll( new_Game_btn,
                                        surrender_btn,
                                        quit_btn );

        border_pane.setBottom( btns_hbox );
    }
    
    @Override
    public void start( Stage stage ) throws FileNotFoundException 
    {
        setupWindow( stage );
        
        setupMenu( stage );

        // FIRST LAUNCH
        newGame();
        
        setupButtons( stage );

        // This would normally be in setupWindow, but it is inluenced by newGame, etc. so it needs to be here.
        border_pane.setCenter( grid );
        
        Scene scene = new Scene( border_pane );
        
        stage.setScene( scene );
        stage.show();
        stage.setTitle( "Memory Game" );
        stage.getIcons().add( new Image("images/card_back.png") );
    }
    
    public static void main( String[] args )
    {
        launch( args );
    }    
}