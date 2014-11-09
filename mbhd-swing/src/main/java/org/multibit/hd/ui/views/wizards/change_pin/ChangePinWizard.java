package org.multibit.hd.ui.views.wizards.change_pin;

import com.google.common.base.Optional;
import org.multibit.hd.ui.views.wizards.AbstractWizard;
import org.multibit.hd.ui.views.wizards.AbstractWizardPanelView;

import java.util.Map;


/**
 * <p>Wizard to provide the following to UI for "change PIN" wizard:</p>
 * <ol>
 * <li>Enter and confirm new PIN</li>
 * </ol>
 *
 * @since 0.0.1
 *
 */
public class ChangePinWizard extends AbstractWizard<ChangePinWizardModel> {

  public ChangePinWizard(ChangePinWizardModel model, boolean isExiting) {
    super(model, isExiting, Optional.absent());
  }

  @Override
  protected void populateWizardViewMap(Map<String, AbstractWizardPanelView> wizardViewMap) {

    wizardViewMap.put(
      ChangePinState.CHANGE_PIN_ENTER_CURRENT_PIN.name(),
      new ChangePinEnterPinPanelView(this, ChangePinState.CHANGE_PIN_ENTER_CURRENT_PIN.name()));
    wizardViewMap.put(
      ChangePinState.CHANGE_PIN_ENTER_NEW_PIN.name(),
      new ChangePinEnterPinPanelView(this, ChangePinState.CHANGE_PIN_ENTER_NEW_PIN.name()));
    wizardViewMap.put(
      ChangePinState.CHANGE_PIN_CONFIRM_NEW_PIN.name(),
      new ChangePinEnterPinPanelView(this, ChangePinState.CHANGE_PIN_CONFIRM_NEW_PIN.name()));
    wizardViewMap.put(
        ChangePinState.CHANGE_PIN_REPORT.name(),
        new ChangePinReportPanelView(this, ChangePinState.CHANGE_PIN_REPORT.name()));
  }
}
