package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class CellController {
    public Label message;
    public Label userName;
    public HBox hBox;

    public CellController()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/listViewCell.fxml"));
        fxmlLoader.setController(this);
        try
        {
            fxmlLoader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setInfo(String message, String userName)
    {
        this.userName.setText(userName);
        this.message.setText(message);
    }

    public HBox getBox()
    {
        return hBox;
    }
}
