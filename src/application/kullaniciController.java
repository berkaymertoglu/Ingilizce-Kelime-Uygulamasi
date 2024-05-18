package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class kullaniciController {

    @FXML
    private Button btn_kelimeEkle;

    @FXML
    private Button btn_sınavOl;
    
    @FXML
    private Button btn_kelimeOgren;

    @FXML
    void btn_kelimeEkle_click(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kelime_ekle.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage(); // Yeni bir stage (pencere) oluşturun
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 

    @FXML
    void btn_sınavOl_click(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kac_soru.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage(); // Yeni bir stage (pencere) oluşturun
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btn_kelimeOgren_click(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kelime_ogren.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage(); // Yeni bir stage (pencere) oluşturun
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}






