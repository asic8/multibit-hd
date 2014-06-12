package org.multibit.hd.ui.views.wizards.payments;

import com.google.common.base.Optional;
import net.miginfocom.swing.MigLayout;
import org.multibit.hd.core.dto.PaymentData;
import org.multibit.hd.core.dto.TransactionData;
import org.multibit.hd.core.exceptions.ExceptionHandler;
import org.multibit.hd.ui.MultiBitUI;
import org.multibit.hd.ui.languages.Languages;
import org.multibit.hd.ui.languages.MessageKey;
import org.multibit.hd.ui.views.components.Buttons;
import org.multibit.hd.ui.views.components.Labels;
import org.multibit.hd.ui.views.components.Panels;
import org.multibit.hd.ui.views.components.TextBoxes;
import org.multibit.hd.ui.views.components.borders.TextBubbleBorder;
import org.multibit.hd.ui.views.components.panels.PanelDecorator;
import org.multibit.hd.ui.views.fonts.AwesomeIcon;
import org.multibit.hd.ui.views.themes.Themes;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;

/**
 * <p>View to provide the following to UI:</p>
 * <ul>
 * <li>Show transaction detail</li>
 * </ul>
 *
 * @since 0.0.1
 *  
 */
public class TransactionDetailPanelView extends AbstractWizardPanelView<PaymentsWizardModel, TransactionDetailPanelModel> {

  private static final String BLOCKCHAIN_INFO_PREFIX = "https://blockchain.info/tx-index/";

  private JLabel transactionHashValue;

  private JTextArea rawTransactionTextArea;

  private JLabel sizeValue;

  /**
   * @param wizard The wizard managing the states
   */
  public TransactionDetailPanelView(AbstractWizard<PaymentsWizardModel> wizard, String panelName) {

    super(wizard, panelName, MessageKey.TRANSACTION_DETAIL, AwesomeIcon.FILE_TEXT_O);

  }

  @Override
  public void newPanelModel() {

    // Configure the panel model
    TransactionDetailPanelModel panelModel = new TransactionDetailPanelModel(
      getPanelName()
    );
    setPanelModel(panelModel);
  }

  @Override
  public void initialiseContent(JPanel contentPanel) {
    contentPanel.setLayout(new MigLayout(
      Panels.migXYLayout(),
      "[][]", // Column constraints
      "[shrink][shrink][grow]" // Row constraints
    ));

    // Apply the theme
    contentPanel.setBackground(Themes.currentTheme.detailPanelBackground());

    JLabel transactionHashLabel = Labels.newTransactionHash();
    transactionHashValue = Labels.newValueLabel("");

    // The raw transaction is a wall of text so needs scroll bars in many cases
    JLabel rawTransactionLabel = Labels.newRawTransaction();
    rawTransactionTextArea = TextBoxes.newReadOnlyTextArea(10, 80);
    rawTransactionTextArea.setBorder(null);

    // Raw transaction requires its own scroll pane
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setOpaque(true);
    scrollPane.setBackground(Themes.currentTheme.readOnlyBackground());
    scrollPane.setBorder(null);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    // View port requires special handling
    scrollPane.setViewportView(rawTransactionTextArea);
    scrollPane.getViewport().setBackground(Themes.currentTheme.readOnlyBackground());
    scrollPane.setViewportBorder(new TextBubbleBorder(Themes.currentTheme.readOnlyBorder()));

    JLabel sizeLabel = Labels.newSize();
    sizeValue = Labels.newValueLabel("");

    JButton blockchainInfoBrowserButton = Buttons.newLaunchBrowserButton(getBlockchainInfoBrowserAction());
    blockchainInfoBrowserButton.setText(Languages.safeText(MessageKey.VIEW_IN_BLOCKCHAIN_INFO));

    contentPanel.add(transactionHashLabel);
    contentPanel.add(transactionHashValue, "shrink,wrap");

    contentPanel.add(sizeLabel);
    contentPanel.add(sizeValue, "shrink,wrap");

    // Consider adding more providers here (buttons break up the information overload)
    contentPanel.add(blockchainInfoBrowserButton, "shrink,alignx left,span 2,wrap");

    contentPanel.add(rawTransactionLabel, "wrap");
    contentPanel.add(scrollPane, "grow,push,span 2," + MultiBitUI.WIZARD_MAX_WIDTH_MIG + ",wrap");

  }

  @Override
  protected void initialiseButtons(AbstractWizard<PaymentsWizardModel> wizard) {
    PanelDecorator.addExitCancelPreviousNext(this, wizard);
  }

  @Override
  public void afterShow() {

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        getNextButton().requestFocusInWindow();
        getNextButton().setEnabled(true);

        PaymentData paymentData = getWizardModel().getPaymentData();
        if (paymentData != null && paymentData instanceof TransactionData) {
          final TransactionData transactionData = (TransactionData) paymentData;

          transactionHashValue.setText(transactionData.getTransactionId());

          // Ensure the raw transaction starts at the beginning
          rawTransactionTextArea.setText(transactionData.getRawTransaction());
          rawTransactionTextArea.setCaretPosition(0);

          int size = transactionData.getSize();
          sizeValue.setText(Languages.safeText(MessageKey.SIZE_VALUE, size));

        }

      }
    });

  }

  @Override
  public void updateFromComponentModels(Optional componentModel) {
    // Do nothing - panel model is updated via an action and wizard model is not applicable
  }

  /**
   * @return The "blockchain info browser" action
   */
  private Action getBlockchainInfoBrowserAction() {

    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {

        try {
          PaymentData paymentData = getWizardModel().getPaymentData();
          if (paymentData != null && paymentData instanceof TransactionData) {
            TransactionData transactionData = (TransactionData) paymentData;
            final URI blockchainInfoURL = URI.create(BLOCKCHAIN_INFO_PREFIX + transactionData.getTransactionId());
            Desktop.getDesktop().browse(blockchainInfoURL);
          }
        } catch (IOException ex) {
          ExceptionHandler.handleThrowable(ex);
        }
      }
    };
  }
}