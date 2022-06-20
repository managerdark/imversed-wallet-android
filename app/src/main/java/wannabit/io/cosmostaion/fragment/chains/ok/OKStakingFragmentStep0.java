package wannabit.io.cosmostaion.fragment.chains.ok;

import static com.fulldive.wallet.models.BaseChain.OKEX_MAIN;
import static com.fulldive.wallet.models.BaseChain.OK_TEST;
import static wannabit.io.cosmostaion.base.BaseConstant.OK_GAS_AMOUNT_STAKE_MUX;
import static wannabit.io.cosmostaion.base.BaseConstant.OK_GAS_RATE_AVERAGE;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.fulldive.wallet.models.BaseChain;
import com.fulldive.wallet.models.Token;

import java.math.BigDecimal;
import java.math.RoundingMode;

import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.activities.chains.ok.OKStakingActivity;
import wannabit.io.cosmostaion.base.BaseConstant;
import wannabit.io.cosmostaion.base.BaseFragment;
import wannabit.io.cosmostaion.dialog.Dialog_Empty_Warning;
import wannabit.io.cosmostaion.model.type.Coin;
import wannabit.io.cosmostaion.utils.WDp;

public class OKStakingFragmentStep0 extends BaseFragment implements View.OnClickListener {

    private Button mCancel, mNextBtn;
    private EditText mAmountInput;
    private TextView mAvailableAmount;
    private TextView mAvailableDenom;
    private ImageView mClearAll;
    private Button mAdd01, mAdd1, mAdd10, mAdd100, mAddHalf, mAddMax;
    private BigDecimal mMaxAvailable = BigDecimal.ZERO;

    private int mDpDecimal = 18;
    private String mDecimalChecker, mDecimalSetter,
            mDecimalDivider2, mDecimalDivider1;

    public static OKStakingFragmentStep0 newInstance(Bundle bundle) {
        OKStakingFragmentStep0 fragment = new OKStakingFragmentStep0();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stake_deposit_0, container, false);
        mCancel = rootView.findViewById(R.id.cancelButton);
        mNextBtn = rootView.findViewById(R.id.nextButton);
        mAmountInput = rootView.findViewById(R.id.et_amount_coin);
        mAvailableAmount = rootView.findViewById(R.id.tv_max_coin);
        mAvailableDenom = rootView.findViewById(R.id.tv_symbol_coin);
        mClearAll = rootView.findViewById(R.id.clearAll);
        mAdd01 = rootView.findViewById(R.id.btn_add_01);
        mAdd1 = rootView.findViewById(R.id.btn_add_1);
        mAdd10 = rootView.findViewById(R.id.btn_add_10);
        mAdd100 = rootView.findViewById(R.id.btn_add_100);
        mAddHalf = rootView.findViewById(R.id.btn_add_half);
        mAddMax = rootView.findViewById(R.id.btn_add_all);
        mCancel.setOnClickListener(this);
        mNextBtn.setOnClickListener(this);
        mClearAll.setOnClickListener(this);
        mAdd01.setOnClickListener(this);
        mAdd1.setOnClickListener(this);
        mAdd10.setOnClickListener(this);
        mAdd100.setOnClickListener(this);
        mAddHalf.setOnClickListener(this);
        mAddMax.setOnClickListener(this);

        mAmountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable et) {
                String es = et.toString().trim();
                if (TextUtils.isEmpty(es)) {
                    mAmountInput.setBackgroundResource(R.drawable.edittext_box);
                } else if (es.startsWith(".")) {
                    mAmountInput.setBackgroundResource(R.drawable.edittext_box);
                    mAmountInput.setText("");
                } else if (es.endsWith(".")) {
                    mAmountInput.setBackgroundResource(R.drawable.edittext_box_error);
                    mAmountInput.setVisibility(View.VISIBLE);
                } else if (es.length() > 1 && es.startsWith("0") && !es.startsWith("0.")) {
                    mAmountInput.setText("0");
                    mAmountInput.setSelection(1);
                }

                if (es.equals(mDecimalChecker)) {
                    mAmountInput.setText(mDecimalSetter);
                    mAmountInput.setSelection(mDpDecimal + 1);

                } else {
                    try {
                        final BigDecimal inputAmount = new BigDecimal(es);
                        if (BigDecimal.ZERO.compareTo(inputAmount) >= 0) {
                            mAmountInput.setBackgroundResource(R.drawable.edittext_box_error);
                            return;
                        }

                        BigDecimal checkPosition = inputAmount.movePointRight(mDpDecimal);
                        BigDecimal checkMax = checkPosition.setScale(0, RoundingMode.DOWN);
                        if (checkPosition.compareTo(checkMax) != 0 || !checkPosition.equals(checkMax)) {
                            String recover = es.substring(0, es.length() - 1);
                            mAmountInput.setText(recover);
                            mAmountInput.setSelection(recover.length());
                            return;
                        }
                        if (getSActivity().getBaseChain().equals(OKEX_MAIN.INSTANCE) || getSActivity().getBaseChain().equals(OK_TEST.INSTANCE)) {
                            if (inputAmount.compareTo(mMaxAvailable) > 0) {
                                mAmountInput.setBackgroundResource(R.drawable.edittext_box_error);
                            } else {
                                mAmountInput.setBackgroundResource(R.drawable.edittext_box);
                            }
                        }

                    } catch (Exception e) {
                    }
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        final BaseChain chain = getSActivity().getBaseChain();
        final Token mainToken = chain.getMainToken();

        WDp.DpMainDenom(getSActivity().getAccount().baseChain, mAvailableDenom);
        if (chain.equals(OKEX_MAIN.INSTANCE) || chain.equals(OK_TEST.INSTANCE)) {
            mDpDecimal = mainToken.getDisplayDecimal();
            setDpDecimals(mDpDecimal);
            int myValidatorCnt = 0;
            if (getBaseDao().mOkStaking != null && getBaseDao().mOkStaking.validator_address != null) {
                myValidatorCnt = getBaseDao().mOkStaking.validator_address.size();
            }
            BigDecimal estimateGasAmount = (new BigDecimal(OK_GAS_AMOUNT_STAKE_MUX).multiply(new BigDecimal("" + myValidatorCnt))).add(new BigDecimal(BaseConstant.OK_GAS_AMOUNT_STAKE));
            BigDecimal feeAmount = estimateGasAmount.multiply(new BigDecimal(OK_GAS_RATE_AVERAGE));
            mMaxAvailable = getSActivity().getAccount().getTokenBalance(mainToken.getDenom()).subtract(feeAmount);
            mAvailableAmount.setText(WDp.getDpAmount2(mMaxAvailable, 0, mDpDecimal));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mCancel)) {
            getSActivity().onBeforeStep();

        } else if (v.equals(mNextBtn)) {
            if (isValidateDepositAmount()) {
                getSActivity().onNextStep();
            } else {
                Toast.makeText(getContext(), R.string.error_invalid_amounts, Toast.LENGTH_SHORT).show();
            }

        } else if (v.equals(mAdd01)) {
            BigDecimal existed = BigDecimal.ZERO;
            String es = mAmountInput.getText().toString().trim();
            if (es.length() > 0) {
                existed = new BigDecimal(es);
            }
            mAmountInput.setText(existed.add(new BigDecimal("0.1")).toPlainString());

        } else if (v.equals(mAdd1)) {
            BigDecimal existed = BigDecimal.ZERO;
            String es = mAmountInput.getText().toString().trim();
            if (es.length() > 0) {
                existed = new BigDecimal(es);
            }
            mAmountInput.setText(existed.add(new BigDecimal("1")).toPlainString());

        } else if (v.equals(mAdd10)) {
            BigDecimal existed = BigDecimal.ZERO;
            String es = mAmountInput.getText().toString().trim();
            if (es.length() > 0) {
                existed = new BigDecimal(es);
            }
            mAmountInput.setText(existed.add(new BigDecimal("10")).toPlainString());

        } else if (v.equals(mAdd100)) {
            BigDecimal existed = BigDecimal.ZERO;
            String es = mAmountInput.getText().toString().trim();
            if (es.length() > 0) {
                existed = new BigDecimal(es);
            }
            mAmountInput.setText(existed.add(new BigDecimal("100")).toPlainString());

        } else if (v.equals(mAddHalf)) {
            if (getSActivity().getBaseChain().equals(OKEX_MAIN.INSTANCE) || getSActivity().getBaseChain().equals(OK_TEST.INSTANCE)) {
                mAmountInput.setText(mMaxAvailable.divide(new BigDecimal("2"), mDpDecimal, RoundingMode.DOWN).toPlainString());
            }

        } else if (v.equals(mAddMax)) {
            if (getSActivity().getBaseChain().equals(OKEX_MAIN.INSTANCE) || getSActivity().getBaseChain().equals(OK_TEST.INSTANCE)) {
                mAmountInput.setText(mMaxAvailable.toPlainString());
            }
            onShowEmptyBlanaceWarnDialog();

        } else if (v.equals(mClearAll)) {
            mAmountInput.setText("");

        }
    }

    private boolean isValidateDepositAmount() {
        try {
            final BaseChain chain = getSActivity().getBaseChain();
            if (chain.equals(OKEX_MAIN.INSTANCE) || chain.equals(OK_TEST.INSTANCE)) {
                BigDecimal depositTemp = new BigDecimal(mAmountInput.getText().toString().trim());
                if (depositTemp.compareTo(BigDecimal.ZERO) <= 0) return false;
                if (depositTemp.compareTo(mMaxAvailable) > 0) return false;
                getSActivity().mToDepositCoin = new Coin(chain.getMainDenom(), depositTemp.setScale(mDpDecimal).toPlainString());
                return true;
            }
            return false;

        } catch (Exception e) {
            getSActivity().mToDepositCoin = null;
            return false;
        }

    }

    private void onShowEmptyBlanaceWarnDialog() {
        Dialog_Empty_Warning dialog = Dialog_Empty_Warning.newInstance();
        showDialog(dialog);
    }

    private void setDpDecimals(int decimals) {
        mDecimalChecker = "0.";
        mDecimalSetter = "0.";
        mDecimalDivider2 = "2";
        mDecimalDivider1 = "1";
        for (int i = 0; i < decimals; i++) {
            mDecimalChecker = mDecimalChecker + "0";
            mDecimalDivider2 = mDecimalDivider2 + "0";
            mDecimalDivider1 = mDecimalDivider1 + "0";
        }
        for (int i = 0; i < decimals - 1; i++) {
            mDecimalSetter = mDecimalSetter + "0";
        }
    }

    private OKStakingActivity getSActivity() {
        return (OKStakingActivity) getBaseActivity();
    }
}
