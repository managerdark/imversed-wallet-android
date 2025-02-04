package wannabit.io.cosmostaion.fragment.chains.kava;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.fulldive.wallet.models.BaseChain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import kava.cdp.v1beta1.Genesis;
import kava.pricefeed.v1beta1.QueryOuterClass;
import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.activities.chains.kava.CreateCdpActivity;
import wannabit.io.cosmostaion.base.BaseFragment;
import wannabit.io.cosmostaion.base.IRefreshTabListener;
import wannabit.io.cosmostaion.dialog.Dialog_Cdp_Warning;
import wannabit.io.cosmostaion.utils.PriceProvider;
import wannabit.io.cosmostaion.utils.WDp;
import wannabit.io.cosmostaion.utils.WUtil;

public class CreateCdpStep3Fragment extends BaseFragment implements View.OnClickListener, IRefreshTabListener {
    public final static int SELECT_CDP_CONFIRM = 9105;

    private TextView mCollateralAmount, mCollateralDenom, mCollateralValue;
    private TextView mPrincipalAmount, mPrincipalDenom, mPrincipalValue;
    private TextView mFeesAmount, mFeesDenom, mFeeValue;
    private TextView mRiskRate;
    private TextView mCurrentPriceTitle, mCurrentPrice;
    private TextView mLiquidationPriceTitle, mLiquidationPrice;
    private TextView mMemo;
    private Button mBeforeBtn, mConfirmBtn;

    public static CreateCdpStep3Fragment newInstance(Bundle bundle) {
        CreateCdpStep3Fragment fragment = new CreateCdpStep3Fragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_cdp_step3, container, false);
        mCollateralAmount = rootView.findViewById(R.id.collateral_amount);
        mCollateralDenom = rootView.findViewById(R.id.collateral_amount_denom);
        mCollateralValue = rootView.findViewById(R.id.collateral_value);
        mPrincipalAmount = rootView.findViewById(R.id.principal_amount);
        mPrincipalDenom = rootView.findViewById(R.id.principal_amount_denom);
        mPrincipalValue = rootView.findViewById(R.id.principal_value);
        mFeesAmount = rootView.findViewById(R.id.fees_amount);
        mFeesDenom = rootView.findViewById(R.id.fees_denom);
        mFeeValue = rootView.findViewById(R.id.fee_value);
        mRiskRate = rootView.findViewById(R.id.risk_rate);
        mCurrentPriceTitle = rootView.findViewById(R.id.current_price_title);
        mCurrentPrice = rootView.findViewById(R.id.current_price);
        mLiquidationPriceTitle = rootView.findViewById(R.id.liquidation_price_title);
        mLiquidationPrice = rootView.findViewById(R.id.liquidation_price);
        mMemo = rootView.findViewById(R.id.memo);
        mBeforeBtn = rootView.findViewById(R.id.btn_before);
        mConfirmBtn = rootView.findViewById(R.id.confirmButton);
        mBeforeBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onRefreshTab() {
        final String cDenom = getCParam().getDenom();
        final String pDenom = getCParam().getDebtLimit().getDenom();
        BigDecimal feeAmount = new BigDecimal(getSActivity().mTxFee.amount.get(0).amount);
        final PriceProvider priceProvider = getSActivity()::getPrice;

        WDp.showCoinDp(getBaseDao(), cDenom, getSActivity().toCollateralAmount.toPlainString(), mCollateralDenom, mCollateralAmount, getSActivity().getBaseChain());
        BigDecimal collateralValue = getSActivity().toCollateralAmount.movePointLeft(WUtil.getKavaCoinDecimal(getBaseDao(), cDenom)).multiply(new BigDecimal(getPrice().getPrice()).movePointLeft(18)).setScale(2, RoundingMode.DOWN);
        mCollateralValue.setText(WDp.getDpRawDollor(getContext(), collateralValue, 2));

        WDp.showCoinDp(getBaseDao(), pDenom, getSActivity().toPrincipalAmount.toPlainString(), mPrincipalDenom, mPrincipalAmount, getSActivity().getBaseChain());
        BigDecimal principalValue = getSActivity().toPrincipalAmount.movePointLeft(WUtil.getKavaCoinDecimal(getBaseDao(), pDenom)).setScale(2, RoundingMode.DOWN);
        mPrincipalValue.setText(WDp.getDpRawDollor(getContext(), principalValue, 2));

        WDp.showCoinDp(getBaseDao(), BaseChain.KAVA_MAIN.INSTANCE.getMainDenom(), feeAmount.toPlainString(), mFeesDenom, mFeesAmount, getSActivity().getBaseChain());
        BigDecimal kavaValue = WDp.usdValue(getBaseDao(), BaseChain.KAVA_MAIN.INSTANCE.getMainDenom(), feeAmount, 6, priceProvider);
        mFeeValue.setText(WDp.getDpRawDollor(getContext(), kavaValue, 2));


        WDp.DpRiskRate(getContext(), getSActivity().mRiskRate, mRiskRate, null);

        mCurrentPriceTitle.setText(String.format(getString(R.string.str_current_title3), cDenom.toUpperCase()));
        mCurrentPrice.setText(WDp.getDpRawDollor(getContext(), new BigDecimal(getPrice().getPrice()).movePointLeft(18).toPlainString(), 4));

        mLiquidationPriceTitle.setText(String.format(getString(R.string.str_liquidation_title3), cDenom.toUpperCase()));
        mLiquidationPrice.setText(WDp.getDpRawDollor(getContext(), getSActivity().mLiquidationPrice.toPlainString(), 4));

        mMemo.setText(getSActivity().mTxMemo);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mBeforeBtn)) {
            getSActivity().onBeforeStep();

        } else if (v.equals(mConfirmBtn)) {
            Dialog_Cdp_Warning dialog = Dialog_Cdp_Warning.newInstance();
            dialog.setTargetFragment(CreateCdpStep3Fragment.this, SELECT_CDP_CONFIRM);
            showDialog(dialog);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_CDP_CONFIRM && resultCode == Activity.RESULT_OK) {
            getSActivity().onStartCreateCdp();
        }
    }


    private CreateCdpActivity getSActivity() {
        return (CreateCdpActivity) getBaseActivity();
    }

    private Genesis.CollateralParam getCParam() {
        return getSActivity().mCollateralParam;
    }

    private QueryOuterClass.CurrentPriceResponse getPrice() {
        return getSActivity().mKavaTokenPrice;
    }
}
