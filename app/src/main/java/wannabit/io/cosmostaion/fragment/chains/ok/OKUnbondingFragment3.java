package wannabit.io.cosmostaion.fragment.chains.ok;

import static com.fulldive.wallet.models.BaseChain.OKEX_MAIN;
import static com.fulldive.wallet.models.BaseChain.OK_TEST;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.math.BigDecimal;

import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.activities.chains.ok.OKUnbondingActivity;
import wannabit.io.cosmostaion.base.BaseFragment;
import wannabit.io.cosmostaion.base.IRefreshTabListener;
import wannabit.io.cosmostaion.utils.WDp;

public class OKUnbondingFragment3 extends BaseFragment implements View.OnClickListener, IRefreshTabListener {

    private TextView mWithdrawAmount;
    private TextView mFeeAmount;
    private TextView mMemo, mTime;
    private Button mBeforeBtn, mConfirmBtn;
    private TextView mWithdrawDenom, mFeeDenom;

    public static OKUnbondingFragment3 newInstance(Bundle bundle) {
        OKUnbondingFragment3 fragment = new OKUnbondingFragment3();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stake_withdraw_3, container, false);
        mWithdrawAmount = rootView.findViewById(R.id.withdraw_amount);
        mWithdrawDenom = rootView.findViewById(R.id.withdraw_amount_denom);
        mFeeAmount = rootView.findViewById(R.id.fees_amount);
        mFeeDenom = rootView.findViewById(R.id.fees_denom);
        mMemo = rootView.findViewById(R.id.memo);
        mTime = rootView.findViewById(R.id.unbonding_time);
        mBeforeBtn = rootView.findViewById(R.id.btn_before);
        mConfirmBtn = rootView.findViewById(R.id.confirmButton);

        WDp.DpMainDenom(getSActivity().getAccount().baseChain, mWithdrawDenom);
        WDp.DpMainDenom(getSActivity().getAccount().baseChain, mFeeDenom);

        mBeforeBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onRefreshTab() {
        BigDecimal toDeleagteAmount = new BigDecimal(getSActivity().mToWithdrawCoin.amount);
        BigDecimal feeAmount = new BigDecimal(getSActivity().mTxFee.amount.get(0).amount);

        if (getSActivity().getBaseChain().equals(OKEX_MAIN.INSTANCE) || getSActivity().getBaseChain().equals(OK_TEST.INSTANCE)) {
            mWithdrawAmount.setText(WDp.getDpAmount2(toDeleagteAmount, 0, 18));
            mFeeAmount.setText(WDp.getDpAmount2(feeAmount, 0, 18));

        }
        mTime.setText(WDp.getUnbondTime(getContext(), getBaseDao(), getSActivity().getBaseChain()));
        mMemo.setText(getSActivity().mTxMemo);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mBeforeBtn)) {
            getSActivity().onBeforeStep();

        } else if (v.equals(mConfirmBtn)) {
            getSActivity().onStartWithdraw();

        }
    }

    private OKUnbondingActivity getSActivity() {
        return (OKUnbondingActivity) getBaseActivity();
    }
}