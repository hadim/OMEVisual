/*
 * The MIT License
 *
 * Copyright 2016 Fiji.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fiji.plugin.omevisual.gui.view;

import fiji.plugin.omevisual.gui.model.GenericModel;
import fiji.plugin.omevisual.gui.model.ImageModel;
import fiji.plugin.omevisual.gui.model.TiffDataModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import loci.formats.ome.OMEXMLMetadata;
import org.scijava.Context;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;

/**
 * FXML Controller class
 *
 * @author Hadrien Mary
 */
public class RootLayoutController implements Initializable {

    @Parameter
    private LogService log;

    @FXML
    private TreeView testTree;

    @FXML
    private CheckBox syncWithImageBox;

    @FXML
    private Label idLabel;

    @FXML
    private Label nameLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setContext(Context context) {
        context.inject(this);
    }

    public void fill(OMEXMLMetadata md) {

        TreeItem<GenericModel<?>> root = new TreeItem<>();
        testTree.setRoot(root);
        testTree.setShowRoot(false);

        // Build and populate the tree
        for (int i = 0; i < md.getImageCount(); i++) {
            ImageModel imageModel = new ImageModel(i, md);
            TreeItem<GenericModel<?>> imageItem = new TreeItem<>(imageModel);
            root.getChildren().add(imageItem);

            for (int j = 0; j < md.getTiffDataCount(i); j++) {
                TiffDataModel dataModel = new TiffDataModel(i, j, md, imageModel);
                //md.getTiffDataFirstC(i, j),
                //      md.getTiffDataFirstT(i, j), md.getTiffDataFirstZ(i, j), md.getTiffDataIFD(i, j));
                TreeItem<GenericModel<?>> dataItem = new TreeItem<>(dataModel);
                imageItem.getChildren().add(dataItem);

            }
        }

        // Handle selection in the tree
        testTree.getSelectionModel().selectedItemProperty().addListener((ObservableValue obs,
                Object o, Object newValue) -> {
            TreeItem<GenericModel<?>> selectedItem = (TreeItem<GenericModel<?>>) newValue;
            GenericModel<?> model = selectedItem.getValue();

            if (model instanceof TiffDataModel) {
                // Display informations relative to TiffData
                populateTiffDataInformations((TiffDataModel) model);
            }

            if (model instanceof ImageModel) {
                // Display informations relative to Image
                populateImageInformations((ImageModel) model);
            }

            if (syncWithImageBox.isSelected()) {
                if (model instanceof TiffDataModel) {
                    // Sync the current selected item with image display
                    log.info("TODO : Sync current TiffData with image display.");
                }
            }
        });

    }

    private void populateImageInformations(ImageModel model) {
        this.idLabel.setText(model.getImageID());
        this.nameLabel.setText(model.getName());
    }

    private void populateTiffDataInformations(TiffDataModel model) {
        ImageModel imageModel = model.getImageModel();
        this.populateImageInformations(imageModel);
        
        // Populate tiffData

    }
}
