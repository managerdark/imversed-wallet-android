package wannabit.io.cosmostaion.fragment;

import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_BORROW_HARD;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_CLAIM_INCENTIVE;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_CREATE_CDP;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_DELETE_ACCOUNT;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_DELETE_DOMAIN;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_DEPOSIT_CDP;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_DEPOSIT_HARD;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_DRAW_DEBT_CDP;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_EXECUTE_CONTRACT;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_GDEX_DEPOSIT;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_GDEX_SWAP;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_GDEX_WITHDRAW;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_IBC_TRANSFER;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_KAVA_EXIT_POOL;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_KAVA_JOIN_POOL;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_KAVA_SWAP;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_LINK_ACCOUNT;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_MINT_NFT;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_OSMOSIS_BEGIN_UNBONDING;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_OSMOSIS_EARNING;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_OSMOSIS_EXIT_POOL;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_OSMOSIS_JOIN_POOL;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_OSMOSIS_SWAP;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_PROFILE;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_REGISTER_ACCOUNT;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_REGISTER_DOMAIN;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_REINVEST;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_RENEW_ACCOUNT;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_RENEW_DOMAIN;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_REPAY_CDP;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_REPAY_HARD;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_REPLACE_STARNAME;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SEND_NFT;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIF_CLAIM_INCENTIVE;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIF_EXIT_POOL;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIF_JOIN_POOL;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIF_SWAP;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIMPLE_CHANGE_REWARD_ADDRESS;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIMPLE_DELEGATE;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIMPLE_REDELEGATE;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIMPLE_REWARD;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIMPLE_SEND;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIMPLE_UNDELEGATE;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_VOTE;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_WITHDRAW_CDP;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_WITHDRAW_HARD;
import static wannabit.io.cosmostaion.base.BaseConstant.NFT_INFURA;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;

import com.addisonelliott.segmentedbutton.SegmentedButtonGroup;
import com.fulldive.wallet.interactors.accounts.AccountsInteractor;
import com.fulldive.wallet.interactors.settings.SettingsInteractor;
import com.fulldive.wallet.models.BaseChain;
import com.fulldive.wallet.models.WalletBalance;
import com.fulldive.wallet.presentation.security.password.CheckPasswordActivity;
import com.google.gson.Gson;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.crypto.DeterministicKey;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;

import cosmos.base.abci.v1beta1.Abci;
import osmosis.lockup.Lock;
import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.base.BaseBroadCastActivity;
import wannabit.io.cosmostaion.base.BaseFragment;
import wannabit.io.cosmostaion.crypto.CryptoHelper;
import wannabit.io.cosmostaion.dao.Account;
import wannabit.io.cosmostaion.dao.StationNFTData;
import wannabit.io.cosmostaion.model.type.Coin;
import wannabit.io.cosmostaion.model.type.Fee;
import wannabit.io.cosmostaion.task.TaskListener;
import wannabit.io.cosmostaion.task.TaskResult;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulChangeRewardAddressGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulClaimRewardsGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulCreateProfileGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulCw20SendGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulDelegateGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulDeleteAccountGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulDeleteDomainGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulGravityDepositGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulGravitySwapGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulGravityWithdrawGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulIBCTransferGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulKavaBorrowHardGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulKavaClaimIncentiveAllGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulKavaCreateCdpGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulKavaDepositCdpGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulKavaDepositGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulKavaDepositHardGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulKavaDrawDebtCdpGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulKavaRepayCdpGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulKavaRepayHardGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulKavaSwapGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulKavaWithDrawCdpGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulKavaWithdrawGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulKavaWithdrawHardGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulLinkAccountGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulMintNFTGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulOsmosisBeginUnbondingGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulOsmosisExitPoolGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulOsmosisJoinPoolGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulOsmosisStartLockGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulOsmosisSwaplnGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulReInvestGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulRedelegateGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulRegisterAccountGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulRegisterDomainGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulRenewAccountGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulRenewDomainGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulReplaceStarNameGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulSendGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulSifDepositGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulSifIncentiveGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulSifSwapGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulSifWithdrawGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulTransferNFTGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulUndelegateGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.simulate.SimulVoteGrpcTask;
import wannabit.io.cosmostaion.utils.PriceProvider;
import wannabit.io.cosmostaion.utils.WDp;
import wannabit.io.cosmostaion.utils.WKey;

public class StepFeeSetFragment extends BaseFragment implements View.OnClickListener, TaskListener {

    private CardView mFeeTotalCard;
    private TextView mFeeDenom, mFeeAmount, mFeeValue;

    private CardView mRateControlCard;
    private TextView mGasAmount, mGasRate, mGasFee;
    private SegmentedButtonGroup mButtonGroup;

    private ImageView mSpeedImg;
    private TextView mSpeedTxt;

    private RelativeLayout mBtnGasCheck;
    private Button mBtnBefore, mBtnNext;


    private int mSelectedGasPosition = 1;
    private BigDecimal mSelectedGasRate = BigDecimal.ZERO;
    private BigDecimal mEstimateGasAmount = BigDecimal.ZERO;
    private BigDecimal mFee = BigDecimal.ZERO;
    private AccountsInteractor accountsInteractor;
    private SettingsInteractor settingsInteractor;

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    simulate();
                }
            }
    );

    public static StepFeeSetFragment newInstance(Bundle bundle) {
        StepFeeSetFragment fragment = new StepFeeSetFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountsInteractor = getAppInjector().getInstance(AccountsInteractor.class);
        settingsInteractor = getAppInjector().getInstance(SettingsInteractor.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tx_step_fee_grpc, container, false);

        mFeeTotalCard = rootView.findViewById(R.id.card_fee_total);
        mFeeDenom = rootView.findViewById(R.id.fee_denom);
        mFeeAmount = rootView.findViewById(R.id.fee_amount);
        mFeeValue = rootView.findViewById(R.id.fee_value);

        mRateControlCard = rootView.findViewById(R.id.rate_control_layer);
        mGasAmount = rootView.findViewById(R.id.gas_amount);
        mGasRate = rootView.findViewById(R.id.gas_rate);
        mGasFee = rootView.findViewById(R.id.gas_fee);
        mButtonGroup = rootView.findViewById(R.id.btns_segmented);

        mSpeedImg = rootView.findViewById(R.id.speed_img);
        mSpeedTxt = rootView.findViewById(R.id.speed_txt);

        mBtnGasCheck = rootView.findViewById(R.id.btn_gas_check);
        mBtnBefore = rootView.findViewById(R.id.btn_before);
        mBtnNext = rootView.findViewById(R.id.nextButton);

        final BaseChain baseChain = getSActivity().getBaseChain();


        WDp.DpMainDenom(baseChain, mFeeDenom);
        mFeeTotalCard.setCardBackgroundColor(WDp.getChainBgColor(getContext(), baseChain));
        mButtonGroup.setSelectedBackground(WDp.getChainColor(getContext(), baseChain));
        mButtonGroup.setRipple(WDp.getChainColor(getContext(), baseChain));
        mEstimateGasAmount = baseChain.getGasProvider().getEstimateGasAmount(getSActivity().mTxType, getSActivity().mValAddresses.size());

        onUpdateView();

        mButtonGroup.setOnPositionChangedListener(position -> {
            mSelectedGasPosition = position;
            onUpdateView();
        });
        mBtnGasCheck.setOnClickListener(this);
        mBtnBefore.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        return rootView;
    }

    private void onCalculateFees() {
        final BaseChain baseChain = getSActivity().getBaseChain();
        mSelectedGasRate = baseChain.getGasRateProvider().get(mSelectedGasPosition);
        if (baseChain.equals(BaseChain.SIF_MAIN.INSTANCE)) {
            mFee = new BigDecimal("100000000000000000");
        } else {
            mFee = mSelectedGasRate.multiply(mEstimateGasAmount).setScale(0, RoundingMode.UP);
        }
    }

    private void onUpdateView() {
        onCalculateFees();
        final PriceProvider priceProvider = getSActivity()::getPrice;

        if (getSActivity().getBaseChain().equals(BaseChain.SIF_MAIN.INSTANCE)) {
            mRateControlCard.setVisibility(View.GONE);
        } else {
            mRateControlCard.setVisibility(View.VISIBLE);
        }
        mFeeAmount.setText(WDp.getDpAmount2(mFee, getSActivity().getBaseChain().getDivideDecimal(), getSActivity().getBaseChain().getDivideDecimal()));
        mFeeValue.setText(WDp.dpUserCurrencyValue(getBaseDao(), settingsInteractor.getCurrency(), getSActivity().getBaseChain().getMainDenom(), mFee, getSActivity().getBaseChain().getDivideDecimal(), priceProvider));

        mGasRate.setText(WDp.getDpGasRate(mSelectedGasRate.toPlainString()));
        mGasAmount.setText(mEstimateGasAmount.toPlainString());
        if (getSActivity().getBaseChain().equals(BaseChain.INJ_MAIN.INSTANCE)) {
            mGasFee.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        }
        mGasFee.setText(mFee.toPlainString());

        if (mSelectedGasPosition == 0) {
            mSpeedImg.setImageResource(R.drawable.bycicle_img);
            mSpeedTxt.setText(R.string.str_fee_speed_title_0);
        } else if (mSelectedGasPosition == 1) {
            mSpeedImg.setImageResource(R.drawable.car_img);
            mSpeedTxt.setText(R.string.str_fee_speed_title_1);
        } else {
            mSpeedImg.setImageResource(R.drawable.rocket_img);
            mSpeedTxt.setText(R.string.str_fee_speed_title_2);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mBtnGasCheck)) {
            onSetFee();
            onSimulateTx();

        } else if (v.equals(mBtnBefore)) {
            getSActivity().onBeforeStep();

        } else if (v.equals(mBtnNext)) {
            if (onCheckValidate()) {
                onSetFee();
                getSActivity().onNextStep();
            }
        }
    }

    private void onSetFee() {
        Fee fee = new Fee();
        Coin gasCoin = new Coin();
        gasCoin.denom = getSActivity().getBaseChain().getMainDenom();
        gasCoin.amount = mFee.toPlainString();
        ArrayList<Coin> amount = new ArrayList<>();
        amount.add(gasCoin);
        fee.amount = amount;
        fee.gas = mEstimateGasAmount.toPlainString();
        getSActivity().mTxFee = fee;

    }

    private boolean onCheckValidate() {
        final String mainDenom = getSActivity().getBaseChain().getMainDenom();
        if (getSActivity().mTxType == CONST_PW_TX_SIMPLE_SEND) {
            final BigDecimal mainDenomAvailable = getSActivity().getBalance(mainDenom);
            if (getSActivity().mDenom.equals(mainDenom)) {
                BigDecimal toSend = new BigDecimal(getSActivity().mAmounts.get(0).amount);
                if ((toSend.add(mFee)).compareTo(mainDenomAvailable) > 0) {
                    Toast.makeText(getContext(), R.string.error_not_enough_fee, Toast.LENGTH_SHORT).show();
                    return false;
                }

            } else {
                if (mFee.compareTo(mainDenomAvailable) > 0) {
                    Toast.makeText(getContext(), R.string.error_not_enough_fee, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

        } else if (getSActivity().mTxType == CONST_PW_TX_SIMPLE_DELEGATE) {
            final WalletBalance balance = getSActivity().getFullBalance(mainDenom);
            BigDecimal delegatable = balance.getBalanceAmount(); // TODO add(getVesting(denom))
            BigDecimal todelegate = new BigDecimal(getSActivity().mAmount.amount);
            if ((todelegate.add(mFee)).compareTo(delegatable) > 0) {
                Toast.makeText(getContext(), R.string.error_not_enough_fee, Toast.LENGTH_SHORT).show();
                return false;
            }

        } else if (getSActivity().mTxType == CONST_PW_TX_SIMPLE_REWARD) {
            BigDecimal available = getSActivity().getBalance(mainDenom);
            if (mFee.compareTo(available) > 0) {
                Toast.makeText(getContext(), R.string.error_not_enough_fee, Toast.LENGTH_SHORT).show();
                return false;
            }
            BigDecimal rewardSum = BigDecimal.ZERO;
            for (String opAddress : getSActivity().mValAddresses) {
                rewardSum = rewardSum.add(getSActivity().getBaseDao().getReward(mainDenom, opAddress));
            }

            if (mFee.compareTo(rewardSum) > 0) {
                Toast.makeText(getContext(), R.string.error_waste_fee, Toast.LENGTH_SHORT).show();
                return false;
            }

        } else if (getSActivity().mTxType == CONST_PW_TX_REINVEST) {
            BigDecimal available = getSActivity().getBalance(mainDenom);
            if (mFee.compareTo(available) > 0) {
                Toast.makeText(getContext(), R.string.error_not_enough_fee, Toast.LENGTH_SHORT).show();
                return false;
            }

            BigDecimal reinvest = new BigDecimal(getSActivity().mAmount.amount);
            if (mFee.compareTo(reinvest) > 0) {
                Toast.makeText(getContext(), R.string.error_waste_fee, Toast.LENGTH_SHORT).show();
                return false;
            }

        }
        return true;
    }

    private BaseBroadCastActivity getSActivity() {
        return (BaseBroadCastActivity) getBaseActivity();
    }

    private void onSimulateTx() {
        launcher.launch(
                new Intent(getSActivity(), CheckPasswordActivity.class),
                ActivityOptionsCompat.makeCustomAnimation(requireContext(), R.anim.slide_in_bottom, R.anim.fade_out)
        );
    }

    public void simulate() {
        getSActivity().showWaitDialog();
        switch (getSActivity().mTxType) {
            case CONST_PW_TX_SIMPLE_SEND:
                new SimulSendGrpcTask(getBaseApplication(), this, getSActivity().getBaseChain(), getSActivity().getAccount(), getSActivity().mToAddress, getSActivity().mAmounts,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_SIMPLE_DELEGATE:
                new SimulDelegateGrpcTask(getBaseApplication(), this, getSActivity().getBaseChain(), getSActivity().getAccount(), getSActivity().mValAddress, getSActivity().mAmount,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_SIMPLE_UNDELEGATE:
                new SimulUndelegateGrpcTask(getBaseApplication(), this, getSActivity().getBaseChain(), getSActivity().getAccount(), getSActivity().mValAddress, getSActivity().mAmount,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_SIMPLE_REDELEGATE:
                new SimulRedelegateGrpcTask(getBaseApplication(), this, getSActivity().getBaseChain(), getSActivity().getAccount(), getSActivity().mValAddress, getSActivity().mToValAddress, getSActivity().mAmount,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_SIMPLE_REWARD:
                new SimulClaimRewardsGrpcTask(getBaseApplication(), this, getSActivity().getBaseChain(), getSActivity().getAccount(), getSActivity().mValAddresses,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_REINVEST:
                new SimulReInvestGrpcTask(getBaseApplication(), this, getSActivity().getBaseChain(), getSActivity().getAccount(), getSActivity().mValAddress, getSActivity().mAmount,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_SIMPLE_CHANGE_REWARD_ADDRESS:
                new SimulChangeRewardAddressGrpcTask(getBaseApplication(), this, getSActivity().getBaseChain(), getSActivity().getAccount(), getSActivity().mNewRewardAddress,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_VOTE:
                new SimulVoteGrpcTask(getBaseApplication(), this, getSActivity().getBaseChain(), getSActivity().getAccount(), getSActivity().mProposalId, getSActivity().mOpinion,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case CONST_PW_TX_REGISTER_DOMAIN:
                new SimulRegisterDomainGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(),
                        getSActivity().mStarNameDomain, getSActivity().mStarNameDomainType,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                break;
            case CONST_PW_TX_REGISTER_ACCOUNT:
                new SimulRegisterAccountGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(),
                        getSActivity().mStarNameDomain, getSActivity().mStarNameAccount, getSActivity().mStarNameResources,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_DELETE_DOMAIN:
                new SimulDeleteDomainGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(),
                        getSActivity().mStarNameDomain,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_DELETE_ACCOUNT:
                new SimulDeleteAccountGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(),
                        getSActivity().mStarNameDomain, getSActivity().mStarNameAccount,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_RENEW_DOMAIN:
                new SimulRenewDomainGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(),
                        getSActivity().mStarNameDomain,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_RENEW_ACCOUNT:
                new SimulRenewAccountGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(),
                        getSActivity().mStarNameDomain, getSActivity().mStarNameAccount,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_REPLACE_STARNAME:
                if (getSActivity().mIsDomain) {
                    new SimulReplaceStarNameGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(),
                            getSActivity().mStarNameDomain, "", getSActivity().mStarNameResources,
                            getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new SimulReplaceStarNameGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(),
                            getSActivity().mStarNameDomain, getSActivity().mStarNameAccount, getSActivity().mStarNameResources,
                            getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                break;
            case CONST_PW_TX_OSMOSIS_SWAP:
                new SimulOsmosisSwaplnGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(),
                        getSActivity().mOsmosisSwapAmountInRoute, getSActivity().mSwapInCoin, getSActivity().mSwapOutCoin,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                break;
            case CONST_PW_TX_OSMOSIS_JOIN_POOL:
                new SimulOsmosisJoinPoolGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(),
                        getSActivity().mOsmosisPoolId, getSActivity().mPoolCoin0, getSActivity().mPoolCoin1, getSActivity().mLpToken.amount,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_OSMOSIS_EXIT_POOL:
                new SimulOsmosisExitPoolGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(),
                        getSActivity().mOsmosisPoolId, getSActivity().mPoolCoin0, getSActivity().mPoolCoin1, getSActivity().mLpToken.amount,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_OSMOSIS_EARNING:
                new SimulOsmosisStartLockGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(),
                        getSActivity().mOsmosisLockupDuration, getSActivity().mLpToken,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_OSMOSIS_BEGIN_UNBONDING:
                ArrayList<Long> tempList = new ArrayList<>();
                for (Lock.PeriodLock lockup : getSActivity().mOsmosisLockups) {
                    tempList.add(lockup.getID());
                }
                new SimulOsmosisBeginUnbondingGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(),
                        tempList, getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case CONST_PW_TX_GDEX_SWAP:
                BigDecimal offerFee = new BigDecimal(getSActivity().mSwapInCoin.amount).multiply(new BigDecimal("0.0015")).setScale(0, RoundingMode.CEILING);
                Coin coinFee = new Coin(getSActivity().mSwapInCoin.denom, offerFee.toPlainString());
                new SimulGravitySwapGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(),
                        getSActivity().mGDexPool.getId(), getSActivity().mSwapInCoin, getSActivity().mSwapOutCoin.denom, coinFee, getSActivity().mGDexSwapOrderPrice,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_GDEX_DEPOSIT:
                new SimulGravityDepositGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(),
                        getSActivity().mGDexPoolId, getSActivity().mPoolCoin0, getSActivity().mPoolCoin1, getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_GDEX_WITHDRAW:
                new SimulGravityWithdrawGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(),
                        getSActivity().mGDexPoolId, getSActivity().mLpToken, getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_IBC_TRANSFER:
                new SimulIBCTransferGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address, getSActivity().mToAddress,
                        getSActivity().mAmounts.get(0).denom, getSActivity().mAmounts.get(0).amount, getSActivity().mPath.port_id, getSActivity().mPath.channel_id, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_SIF_CLAIM_INCENTIVE:
                new SimulSifIncentiveGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_SIF_SWAP:
                new SimulSifSwapGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address,
                        getSActivity().mSifSwapInCoin.denom, getSActivity().mSifSwapInCoin.amount, getSActivity().mSifSwapOutCoin.denom, getSActivity().mSifSwapOutCoin.amount,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_SIF_JOIN_POOL:
                new SimulSifDepositGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address,
                        getSActivity().mSifDepositCoin1.denom, getSActivity().mSifDepositCoin0.amount, getSActivity().mSifDepositCoin1.amount,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_SIF_EXIT_POOL:
                BigDecimal myShareAllAmount = new BigDecimal(getSActivity().mMyProvider.getLiquidityProvider().getLiquidityProviderUnits());
                BigDecimal myShareWithdrawAmount = new BigDecimal(getSActivity().mSifWithdrawCoin.amount);
                String basisPoint = myShareWithdrawAmount.movePointRight(4).divide(myShareAllAmount, 0, RoundingMode.DOWN).toPlainString();
                new SimulSifWithdrawGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address,
                        getSActivity().mSifWithdrawCoin.denom, basisPoint, getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case CONST_PW_TX_MINT_NFT:
                StationNFTData nftData = new StationNFTData(getSActivity().getAccount().address, getSActivity().mNftName, getSActivity().mNftDescription, getSActivity().mNftDenomId, NFT_INFURA + getSActivity().mNftHash);
                Gson gson = new Gson();
                String jsonData = gson.toJson(nftData);
                new SimulMintNFTGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address,
                        getSActivity().mNftDenomId, getSActivity().mNftDenomName, getSActivity().mNftHash.toLowerCase(), getSActivity().mNftName, NFT_INFURA + getSActivity().mNftHash, jsonData,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_SEND_NFT:
                new SimulTransferNFTGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address,
                        getSActivity().mToAddress, getSActivity().mNftDenomId, getSActivity().mNftTokenId, getSActivity().mIrisResponse, getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case CONST_PW_TX_PROFILE:
                String profileUri = "";
                String coverUri = "";
                if (getSActivity().mProfileImg != null) {
                    profileUri = "https://ipfs.infura.io/ipfs/" + getSActivity().mProfileImg;
                } else {
                    profileUri = "";
                }
                if (getSActivity().mCoverImg != null) {
                    coverUri = "https://ipfs.infura.io/ipfs/" + getSActivity().mCoverImg;
                } else {
                    coverUri = "";
                }
                new SimulCreateProfileGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().mDtag, getSActivity().mNickname, getSActivity().mBio,
                        profileUri, coverUri, getSActivity().getAccount().address, getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_LINK_ACCOUNT:
                Account toAccount = accountsInteractor.getAccount(getSActivity().mDesmosToLinkAccountId).blockingGet();
                ECKey ecKey;
                if (toAccount.fromMnemonic) {
                    String entropy = CryptoHelper.decryptData(getSActivity().getString(R.string.key_mnemonic) + toAccount.uuid, toAccount.resource, toAccount.spec);
                    DeterministicKey deterministicKey = WKey.getKeyWithPathfromEntropy(toAccount, entropy);
                    ecKey = ECKey.fromPrivate(new BigInteger(deterministicKey.getPrivateKeyAsHex(), 16));
                } else {
                    String privateKey = CryptoHelper.decryptData(getSActivity().getString(R.string.key_private) + toAccount.uuid, toAccount.resource, toAccount.spec);
                    ecKey = ECKey.fromPrivate(new BigInteger(privateKey, 16));
                }
                new SimulLinkAccountGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address, getSActivity().mDesmosToLinkChain,
                        toAccount, ecKey, getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_KAVA_SWAP:
                new SimulKavaSwapGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address, getSActivity().mKavaSwapIn, getSActivity().mKavaSwapOut,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_KAVA_JOIN_POOL:
                new SimulKavaDepositGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address, getSActivity().mKavaPoolTokenA, getSActivity().mKavaPoolTokenB,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_KAVA_EXIT_POOL:
                new SimulKavaWithdrawGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address, getSActivity().mKavaShareAmount.toPlainString(), getSActivity().mKavaMinTokenA, getSActivity().mKavaMinTokenB,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_CREATE_CDP:
                new SimulKavaCreateCdpGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address, getSActivity().mCollateral, getSActivity().mPrincipal,
                        getSActivity().mCollateralType, getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_DEPOSIT_CDP:
                new SimulKavaDepositCdpGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address, getSActivity().getAccount().address, getSActivity().mCollateral,
                        getSActivity().mCollateralType, getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_WITHDRAW_CDP:
                new SimulKavaWithDrawCdpGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address, getSActivity().getAccount().address, getSActivity().mCollateral,
                        getSActivity().mCollateralType, getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_DRAW_DEBT_CDP:
                new SimulKavaDrawDebtCdpGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address, getSActivity().mPrincipal,
                        getSActivity().mCollateralType, getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_REPAY_CDP:
                new SimulKavaRepayCdpGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address, getSActivity().mPayment,
                        getSActivity().mCollateralType, getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_DEPOSIT_HARD:
                new SimulKavaDepositHardGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address, getSActivity().mHardPoolCoins,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_WITHDRAW_HARD:
                new SimulKavaWithdrawHardGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address, getSActivity().mHardPoolCoins,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_BORROW_HARD:
                new SimulKavaBorrowHardGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address, getSActivity().mHardPoolCoins,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_REPAY_HARD:
                new SimulKavaRepayHardGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address, getSActivity().getAccount().address, getSActivity().mHardPoolCoins,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_CLAIM_INCENTIVE:
                new SimulKavaClaimIncentiveAllGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address, getSActivity().mIncentiveMultiplier, getSActivity().getBaseDao(),
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                break;
            case CONST_PW_TX_EXECUTE_CONTRACT:
                new SimulCw20SendGrpcTask(getBaseApplication(), this, getSActivity().getAccount(), getSActivity().getBaseChain(), getSActivity().getAccount().address, getSActivity().mToAddress, getSActivity().mContractAddress, getSActivity().mAmounts,
                        getSActivity().mTxMemo, getSActivity().mTxFee, getBaseDao().getChainIdGrpc()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
        }
    }

    @Override
    public void onTaskResponse(TaskResult result) {
        if (result.isSuccess && result.resultData != null) {
            Abci.GasInfo gasInfo = ((Abci.GasInfo) result.resultData);
            long gasused = gasInfo.getGasUsed();
            gasused = (long) ((double) gasused * 1.1d);
            mEstimateGasAmount = new BigDecimal(gasused);
            onUpdateView();
            Toast.makeText(getContext(), R.string.str_apply_estimate_gas_amount, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.str_network_error_title, Toast.LENGTH_SHORT).show();
        }
        getSActivity().hideWaitDialog();
    }
}
