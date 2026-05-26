package edu.ntnu.idi.idatt.view.util;

import edu.ntnu.idi.idatt.view.component.ErrorWidget;
import edu.ntnu.idi.idatt.view.component.InfoWidget;
import java.math.BigDecimal;
import java.util.function.BiConsumer;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import javafx.util.Callback;

/**
 * Utility class providing reusable JavaFX helpers for the view layer.
 *
 * <p>Includes convenience methods for sign-based CSS styling, coloured table cells,
 * rounded-corner clipping, double-click row handlers, and modal alert dialogs.
 * This class is not instantiable; all methods are static.</p>
 */
public class ViewUtility {

  /** Prevents instantiation of this static utility class. */
  private ViewUtility() {}

  /**
   * Applies a {@code text-positive} or {@code text-negative} CSS class to a labeled node
   * based on the sign of the given value.
   *
   * <p>Any previously applied sign class is removed before the new one is added. If the
   * value is exactly zero, no sign class is applied.</p>
   *
   * @param node  the labeled node to style; must not be {@code null}
   * @param value the value whose sign determines the applied CSS class; must not be
   *              {@code null}
   */
  public static void applySignStyleClass(Labeled node, BigDecimal value) {
    node.getStyleClass().removeAll("text-positive", "text-negative");
    int cmp = value.compareTo(BigDecimal.ZERO);
    if (cmp > 0) {
      node.getStyleClass().add("text-positive");
    } else if (cmp < 0) {
      node.getStyleClass().add("text-negative");
    }
  }

  /**
   * Returns a cell factory that colours a string cell green if its text contains {@code +}
   * or red if it contains {@code -}.
   *
   * <p>The factory applies the {@code text-positive} or {@code text-negative} CSS class
   * respectively, and clears both classes when the cell is empty or its value changes.</p>
   *
   * @param <S> the type of the table row item
   * @return a {@link Callback} suitable for use with
   *         {@link TableColumn#setCellFactory(Callback)}
   */
  public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> coloredStringCellFactory() {
    return col -> new TableCell<>() {
      /** {@inheritDoc} */
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        getStyleClass().removeAll("text-positive", "text-negative");
        if (empty || item == null) {
          setText(null);
        } else {
          setText(item);
          if (item.contains("+")) {
            getStyleClass().add("text-positive");
          } else if (item.contains("-")) {
            getStyleClass().add("text-negative");
          }
        }
      }
    };
  }

  /**
   * Applies a dynamically sized rounded-corner clip to the given region.
   *
   * <p>Listeners are attached to the region's width and height properties so the clip
   * rectangle resizes automatically when the region's dimensions change.</p>
   *
   * @param node      the region to clip; must not be {@code null}
   * @param arcRadius the corner arc radius in pixels; controls how rounded the corners appear
   */
  public static void applyRoundedClip(Region node, double arcRadius) {
    Rectangle clip = new Rectangle();
    clip.setArcWidth(arcRadius * 2);
    clip.setArcHeight(arcRadius * 2);
    node.widthProperty().addListener((obs, old, w) -> clip.setWidth(w.doubleValue()));
    node.heightProperty().addListener((obs, old, h) -> clip.setHeight(h.doubleValue()));
    node.setClip(clip);
  }

  /**
   * Returns a row factory that invokes a callback when the user double-clicks a non-empty row.
   *
   * <p>The callback receives the row's item and the owning window, so it can open a
   * detail dialog anchored to the correct parent.</p>
   *
   * @param <T>         the type of the table row item
   * @param onDoubleClick the callback to invoke on a primary-button double-click; receives
   *                      the row item and the owning {@link Window}
   * @return a {@link Callback} suitable for use with {@link TableView#setRowFactory(Callback)}
   */
  public static <T> Callback<TableView<T>, TableRow<T>> doubleClickRowFactory(
      BiConsumer<T, Window> onDoubleClick) {
    return tv -> {
      TableRow<T> row = new TableRow<>();
      row.setOnMouseClicked(event -> {
        if (!row.isEmpty()
            && event.getButton() == MouseButton.PRIMARY
            && event.getClickCount() == 2) {
          onDoubleClick.accept(row.getItem(), row.getScene().getWindow());
        }
      });
      return row;
    };
  }

  /**
   * Displays a modal error alert dialog anchored to the currently showing window.
   *
   * @param title   the title displayed in the alert header
   * @param message the error description shown in the alert body
   */
  public static void showErrorAlert(String title, String message) {
    Window owner = Window.getWindows().stream()
        .filter(Window::isShowing)
        .reduce((a, b) -> b)
        .orElse(null);
    new ErrorWidget(title, message).openDialog(owner);
  }

  /**
   * Displays a modal informational alert dialog anchored to the currently showing window.
   *
   * @param title   the title displayed in the alert header
   * @param message the informational text shown in the alert body
   */
  public static void showSuccessAlert(String title, String message) {
    Window owner = Window.getWindows().stream()
        .filter(Window::isShowing)
        .reduce((a, b) -> b)
        .orElse(null);
    new InfoWidget(title, message).openDialog(owner);
  }
}
