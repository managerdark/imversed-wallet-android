package wannabit.io.cosmostaion.widget.mainWallet;

import static wannabit.io.cosmostaion.base.BaseConstant.OK_GAS_AMOUNT_STAKE_MUX;
import static wannabit.io.cosmostaion.base.BaseConstant.OK_GAS_RATE_AVERAGE;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fulldive.wallet.interactors.accounts.AccountsInteractor;
import com.fulldive.wallet.interactors.settings.SettingsInteractor;
import com.fulldive.wallet.models.WalletBalance;
import com.fulldive.wallet.presentation.main.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.activities.chains.ok.OKStakingActivity;
import wannabit.io.cosmostaion.activities.chains.ok.OKUnbondingActivity;
import wannabit.io.cosmostaion.activities.chains.ok.OKValidatorListActivity;
import wannabit.io.cosmostaion.base.BaseConstant;
import wannabit.io.cosmostaion.base.BaseData;
import wannabit.io.cosmostaion.dialog.Dialog_WatchMode;
import wannabit.io.cosmostaion.utils.PriceProvider;
import wannabit.io.cosmostaion.utils.WDp;
import wannabit.io.cosmostaion.widget.BaseHolder;

public class WalletOkexHolder extends BaseHolder {
    private final TextView mOkTotalAmount;
    private final TextView mOkTotalValue;
    private final TextView mOkAvailable;
    private final TextView mOkLocked;
    private final TextView mOkDeposit;
    private final TextView mOkWithdrawing;
    private final RelativeLayout mBtnOkDeposit;
    private final RelativeLayout mBtnOkWithdraw;
    private final RelativeLayout mBtnOkVoteForVali;
    private final RelativeLayout mBtnOkVote;

    public WalletOkexHolder(@NonNull View itemView) {
        super(itemView);
        mOkTotalAmount = itemView.findViewById(R.id.ok_total_amount);
        mOkTotalValue = itemView.findViewById(R.id.ok_total_value);
        mOkAvailable = itemView.findViewById(R.id.ok_available);
        mOkLocked = itemView.findViewById(R.id.ok_locked);
        mOkDeposit = itemView.findViewById(R.id.ok_deposit);
        mOkWithdrawing = itemView.findViewById(R.id.ok_withdrawing);
        mBtnOkDeposit = itemView.findViewById(R.id.btn_ok_deposit);
        mBtnOkWithdraw = itemView.findViewById(R.id.btn_ok_withdraw);
        mBtnOkVoteForVali = itemView.findViewById(R.id.btn_ok_vote_for_validator);
        mBtnOkVote = itemView.findViewById(R.id.btn_ok_vote);
    }

    public void onBindHolder(@NotNull MainActivity mainActivity) {
        final SettingsInteractor settingsInteractor = mainActivity.getAppInjector().getInstance(SettingsInteractor.class);
        final BaseData baseData = mainActivity.getBaseDao();
        final String denom = mainActivity.getBaseChain().getMainDenom();
        final PriceProvider priceProvider = mainActivity::getPrice;

        final WalletBalance balance = mainActivity.getFullBalance(denom);
        final BigDecimal availableAmount = balance.getBalanceAmount();
        final BigDecimal lockedAmount = balance.getLockedAmount();
        final BigDecimal depositAmount = baseData.okDepositAmount();
        final BigDecimal withdrawAmount = baseData.okWithdrawAmount();
        final BigDecimal totalAmount = availableAmount.add(lockedAmount).add(depositAmount).add(withdrawAmount);

        mOkTotalAmount.setText(WDp.getDpAmount2(totalAmount, 0, 6));
        mOkAvailable.setText(WDp.getDpAmount2(availableAmount, 0, 6));
        mOkLocked.setText(WDp.getDpAmount2(lockedAmount, 0, 6));
        mOkDeposit.setText(WDp.getDpAmount2(depositAmount, 0, 6));
        mOkWithdrawing.setText(WDp.getDpAmount2(withdrawAmount, 0, 6));
        mOkTotalValue.setText(WDp.dpUserCurrencyValue(baseData, settingsInteractor.getCurrency(), denom, totalAmount, 0, priceProvider));

        mainActivity.getAppInjector().getInstance(AccountsInteractor.class).updateLastTotal(mainActivity.getAccount().id, totalAmount.toPlainString());

        mBtnOkDeposit.setOnClickListener(v -> {
            if (mainActivity.getBaseDao().mTopValidators == null && mainActivity.getBaseDao().mTopValidators.size() == 0)
                return;
            if (!mainActivity.getAccount().hasPrivateKey) {
                Dialog_WatchMode add = Dialog_WatchMode.newInstance();
                mainActivity.showDialog(add);
                return;
            }
            int myValidatorCnt = 0;
            if (mainActivity.getBaseDao().mOkStaking != null && mainActivity.getBaseDao().mOkStaking.validator_address != null) {
                myValidatorCnt = mainActivity.getBaseDao().mOkStaking.validator_address.size();
            }
            BigDecimal estimateGasAmount = (new BigDecimal(OK_GAS_AMOUNT_STAKE_MUX).multiply(new BigDecimal("" + myValidatorCnt))).add(new BigDecimal(BaseConstant.OK_GAS_AMOUNT_STAKE));
            BigDecimal feeAmount = estimateGasAmount.multiply(new BigDecimal(OK_GAS_RATE_AVERAGE));
            if (availableAmount.compareTo(feeAmount) <= 0) {
                Toast.makeText(mainActivity, R.string.error_not_enough_fee, Toast.LENGTH_SHORT).show();
                return;
            }
            if (availableAmount.compareTo(new BigDecimal("0.01")) < 0) {
                Toast.makeText(mainActivity, R.string.error_not_enough_to_deposit, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(mainActivity, OKStakingActivity.class);
            mainActivity.startActivity(intent);
        });
        mBtnOkWithdraw.setOnClickListener(v -> {
            if (mainActivity.getBaseDao().mTopValidators == null && mainActivity.getBaseDao().mTopValidators.size() == 0)
                return;
            if (!mainActivity.getAccount().hasPrivateKey) {
                Dialog_WatchMode add = Dialog_WatchMode.newInstance();
                mainActivity.showDialog(add);
                return;
            }

            if ((mainActivity.getBaseDao().okDepositAmount()).compareTo(BigDecimal.ZERO) <= 0) {
                Toast.makeText(mainActivity, R.string.error_not_enough_to_withdraw, Toast.LENGTH_SHORT).show();
                return;
            }

            int myValidatorCnt = 0;
            if (mainActivity.getBaseDao().mOkStaking != null && mainActivity.getBaseDao().mOkStaking.validator_address != null) {
                myValidatorCnt = mainActivity.getBaseDao().mOkStaking.validator_address.size();
            }
            BigDecimal estimateGasAmount = (new BigDecimal(OK_GAS_AMOUNT_STAKE_MUX).multiply(new BigDecimal("" + myValidatorCnt))).add(new BigDecimal(BaseConstant.OK_GAS_AMOUNT_STAKE));
            BigDecimal feeAmount = estimateGasAmount.multiply(new BigDecimal(OK_GAS_RATE_AVERAGE));
            if (availableAmount.compareTo(feeAmount) <= 0) {
                Toast.makeText(mainActivity, R.string.error_not_enough_fee, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(mainActivity, OKUnbondingActivity.class);
            mainActivity.startActivity(intent);
        });
        mBtnOkVoteForVali.setOnClickListener(v -> {
            Intent intent = new Intent(mainActivity, OKValidatorListActivity.class);
            mainActivity.startActivity(intent);
        });
        mBtnOkVote.setOnClickListener(v -> {
//                mainActivity.startActivity(new Intent(mainActivity, VoteListActivity.class));
            Toast.makeText(mainActivity, R.string.error_prepare, Toast.LENGTH_SHORT).show();
        });
    }
}
