package wannabit.io.cosmostaion.fragment.chains.starname;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.math.BigDecimal;

import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.activities.chains.starname.RegisterStarNameDomainActivity;
import wannabit.io.cosmostaion.base.BaseFragment;
import wannabit.io.cosmostaion.base.IRefreshTabListener;
import wannabit.io.cosmostaion.utils.WDp;

public class RegisterDomain3Fragment extends BaseFragment implements View.OnClickListener, IRefreshTabListener {

    private Button mBeforeBtn, mConfirmBtn;
    private TextView mFeeAmount, mStarnameFeeAmount;
    private TextView mDomain, mExpireTime, mDomainType, mMemo;

    public static RegisterDomain3Fragment newInstance(Bundle bundle) {
        RegisterDomain3Fragment fragment = new RegisterDomain3Fragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register_domain3, container, false);
        mBeforeBtn = rootView.findViewById(R.id.btn_before);
        mConfirmBtn = rootView.findViewById(R.id.confirmButton);
        mFeeAmount = rootView.findViewById(R.id.tx_fee_amount);
        mStarnameFeeAmount = rootView.findViewById(R.id.starname_fee_amount);
        mDomain = rootView.findViewById(R.id.domain_name);
        mExpireTime = rootView.findViewById(R.id.expire_time);
        mDomainType = rootView.findViewById(R.id.domain_type);
        mMemo = rootView.findViewById(R.id.memo);
        mBeforeBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onRefreshTab() {
        BigDecimal starnameFeeAmount = getBaseDao().getStarNameRegisterDomainFee(getSActivity().mStarNameDomain, getSActivity().mStarNameDomainType);
        BigDecimal feeAmount = new BigDecimal(getSActivity().mTxFee.amount.get(0).amount);

        mFeeAmount.setText(WDp.getDpAmount2(feeAmount, 6, 6));
        mStarnameFeeAmount.setText(WDp.getDpAmount2(starnameFeeAmount, 6, 6));
        mExpireTime.setText(WDp.getDpTime(getContext(), getBaseDao().getStarNameRegisterDomainExpireTime()));

        mDomain.setText("*" + getSActivity().mStarNameDomain);
        mDomainType.setText(getSActivity().mStarNameDomainType);
        mMemo.setText(getSActivity().mTxMemo);
    }


    @Override
    public void onClick(View v) {
        if (v.equals(mBeforeBtn)) {
            getSActivity().onBeforeStep();

        } else if (v.equals(mConfirmBtn)) {
            getSActivity().onStartRegDomain();
        }
    }

    private RegisterStarNameDomainActivity getSActivity() {
        return (RegisterStarNameDomainActivity) getBaseActivity();
    }
}