import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;

/**
 * The InputFrameController class.  It controls input from the users and validates it.
 * If validation is successful, the Adjacency game screen will pop up in a different window.
 *
 * @author Jedid Ahn
 *
 */
public class InputFrameController{

    @FXML
    public ComboBox<String> gameMode;

    @FXML
    public ComboBox<String> algorithmBot1;

    @FXML
    public ComboBox<String> algorithmBot2;

    @FXML
    public Label player1Label;

    @FXML
    public Label player2Label;

    @FXML
    private TextField player1;

    @FXML
    private TextField player2;

    @FXML
    private ComboBox<String> numberOfRounds;

    @FXML
    public Label isBotFirstLabel;

    @FXML
    public CheckBox isBotFirst;


    /**
     * Initialize the dropdown ComboBox with a list of items that are allowed to be selected.
     * Select the first item in the list as the default value of the dropdown.
     *
     */
    @FXML
    private void initialize(){
        ObservableList<String> gameModeDropdown = FXCollections.observableArrayList(
                "Player vs Bot", "Bot vs Bot");
        this.gameMode.setItems(gameModeDropdown);
        this.gameMode.getSelectionModel().select(0);
        this.gameMode.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> updateGameMode(newValue)
        );

        ObservableList<String> algorithmBotDropdown = FXCollections.observableArrayList(
                "Minimax", "Local Search", "Genetic");
        this.algorithmBot1.setItems(algorithmBotDropdown);
        this.algorithmBot1.getSelectionModel().select(0);
        this.algorithmBot1.setDisable(true);
        this.algorithmBot2.setItems(algorithmBotDropdown);
        this.algorithmBot2.getSelectionModel().select(0);

        ObservableList<String> numberOfRoundsDropdown = FXCollections.observableArrayList(
                "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
                "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28");
        this.numberOfRounds.setItems(numberOfRoundsDropdown);
        this.numberOfRounds.getSelectionModel().select(0);
    }

    @FXML
    private void updateGameMode(String newGameMode) {
         if (newGameMode.equals("Bot vs Bot")) {
            this.player1Label.setText("Bot-1 (X) Name:");
            this.player2Label.setText("Bot-2 (O) Name:");
            this.algorithmBot1.setDisable(false);
            this.isBotFirstLabel.setText("Bot-2 (O) goes first:");
        } else {
             this.player1Label.setText("Player (X) Name:");
             this.player2Label.setText("Bot (O) Name:");
             this.algorithmBot1.setDisable(true);
             this.isBotFirstLabel.setText("Bot (O) goes first:");
         }
    }

    /**
     * Reset player1 and player2 text fields and reset numberOfRounds dropdown to default value
     * if reset button is clicked.
     *
     */
    @FXML
    private void reset(){
        this.player1.setText("");
        this.player2.setText("");
        this.numberOfRounds.getSelectionModel().select(0);
    }


    /**
     * Open OutputFrame controlled by OutputFrameController if play button is clicked and
     * all input have been successfully validated.
     *
     * @exception IOException To load the FXMLLoader to open the Adjacency game screen (output screen).
     *
     */
    @FXML
    private void play() throws IOException {
        if (this.isInputFieldValidated()) {
            // Close primary stage/input frame.
            Stage primaryStage = (Stage) this.player1.getScene().getWindow();
            primaryStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("OutputFrame.fxml"));
            Parent root = loader.load();

            // Get controller of output frame and pass input including player names and number of rounds chosen.
            OutputFrameController outputFC = loader.getController();
            outputFC.getInput(this.gameMode.getValue(), this.algorithmBot1.getValue(), this.algorithmBot2.getValue(),
                    this.player1.getText(), this.player2.getText(), this.numberOfRounds.getValue(),
                    this.isBotFirst.isSelected());

            // Open the new frame.
            Stage secondaryStage = new Stage();
            secondaryStage.setTitle("Game Board Display");
            secondaryStage.setScene(new Scene(root));
            secondaryStage.setResizable(true);
            secondaryStage.show();

            Platform.runLater(outputFC::startGame);
        }
    }


    /**
     * Return whether all input fields have been successfully validated or not.
     *
     * @return boolean
     *
     */
    private boolean isInputFieldValidated() {
        String chosenGameMode = this.gameMode.getValue();
        String chosenAlgorithmBot1 = this.algorithmBot1.getValue();
        String chosenAlgorithmBot2 = this.algorithmBot2.getValue();
        String playerX = this.player1.getText();
        String playerO = this.player2.getText();
        String roundNumber = this.numberOfRounds.getValue();

        if (playerX.length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Player 1 name is blank.").showAndWait();
            return false;
        }

        if (playerO.length() == 0) {
            new Alert(Alert.AlertType.ERROR, "Player 2 name is blank.").showAndWait();
            return false;
        }

        if (playerX.equals(playerO)){
            new Alert(Alert.AlertType.ERROR, "Player 1 and Player 2 cannot have the same name.").showAndWait();
            return false;
        }

        return true;
    }
}