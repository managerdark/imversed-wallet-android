package wannabit.io.cosmostaion.activities;

import static com.fulldive.wallet.models.BaseChain.ALTHEA_TEST;
import static com.fulldive.wallet.models.BaseChain.BAND_MAIN;
import static cosmos.staking.v1beta1.Staking.BondStatus.BOND_STATUS_BONDED;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_REINVEST;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIMPLE_DELEGATE;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIMPLE_REDELEGATE;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIMPLE_REWARD;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIMPLE_UNDELEGATE;
import static wannabit.io.cosmostaion.base.BaseConstant.TASK_GRPC_FETCH_ALL_REWARDS;
import static wannabit.io.cosmostaion.base.BaseConstant.TASK_GRPC_FETCH_DELEGATIONS;
import static wannabit.io.cosmostaion.base.BaseConstant.TASK_GRPC_FETCH_REDELEGATIONS_TO;
import static wannabit.io.cosmostaion.base.BaseConstant.TASK_GRPC_FETCH_SELF_BONDING;
import static wannabit.io.cosmostaion.base.BaseConstant.TASK_GRPC_FETCH_UNDELEGATIONS;
import static wannabit.io.cosmostaion.base.BaseConstant.TASK_GRPC_FETCH_VALIDATOR_INFO;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fulldive.wallet.interactors.secret.WalletUtils;
import com.fulldive.wallet.models.BaseChain;
import com.fulldive.wallet.models.WalletBalance;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cosmos.distribution.v1beta1.Distribution;
import cosmos.staking.v1beta1.Staking;
import de.hdodenhof.circleimageview.CircleImageView;
import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.base.BaseActivity;
import wannabit.io.cosmostaion.base.BaseConstant;
import wannabit.io.cosmostaion.dialog.Dialog_Not_Top_100;
import wannabit.io.cosmostaion.dialog.Dialog_RedelegationLimited;
import wannabit.io.cosmostaion.dialog.Dialog_WatchMode;
import wannabit.io.cosmostaion.model.type.Coin;
import wannabit.io.cosmostaion.network.res.ResApiNewTxListCustom;
import wannabit.io.cosmostaion.task.FetchTask.ApiStakeTxsHistoryTask;
import wannabit.io.cosmostaion.task.TaskListener;
import wannabit.io.cosmostaion.task.TaskResult;
import wannabit.io.cosmostaion.task.gRpcTask.AllRewardGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.DelegationsGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.ReDelegationsToGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.SelfBondingGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.UnDelegationsGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.ValidatorInfoGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.WithdrawAddressGrpcTask;
import wannabit.io.cosmostaion.utils.WDp;
import wannabit.io.cosmostaion.utils.WUtil;

public class ValidatorActivity extends BaseActivity implements TaskListener {

    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private int mTaskCount;

    private ValidatorAdapter mValidatorAdapter;

    //gRPC
    private String mValOpAddress;
    private Staking.Validator mGrpcValidator;
    private Staking.DelegationResponse mGrpcMyDelegation;
    private Staking.UnbondingDelegation mGrpcMyUndelegation;
    private Staking.DelegationResponse mGrpcSelfDelegation;
    private List<Staking.RedelegationResponse> mGrpcRedelegates;
    private ArrayList<ResApiNewTxListCustom> mApiNewTxCustomHistory = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validator);
        mToolbar = findViewById(R.id.toolbar);
        mSwipeRefreshLayout = findViewById(R.id.layer_refresher);
        mRecyclerView = findViewById(R.id.recycler);

        mValOpAddress = getIntent().getStringExtra("valOpAddress");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            onInitFetch();
            onFetchValHistory();

        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setHasFixedSize(true);
        showWaitDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getAccount() == null) {
            onBackPressed();
        }

        mValidatorAdapter = new ValidatorAdapter();
        mRecyclerView.setAdapter(mValidatorAdapter);

        onInitFetch();
        onFetchValHistory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onInitFetch() {
        if (mTaskCount > 0) return;
        mTaskCount = 6;
        getBaseDao().mGrpcDelegations = new ArrayList<>();
        getBaseDao().mGrpcUndelegations = new ArrayList<>();
        getBaseDao().mGrpcRewards = new ArrayList<>();

        new ValidatorInfoGrpcTask(getBaseApplication(), this, getBaseChain(), mValOpAddress).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new DelegationsGrpcTask(getBaseApplication(), this, getBaseChain(), getAccount()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new SelfBondingGrpcTask(getBaseApplication(), this, getBaseChain(), mValOpAddress, WalletUtils.INSTANCE.convertDpOpAddressToDpAddress(mValOpAddress, BaseChain.getChain(getAccount().baseChain))).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new UnDelegationsGrpcTask(getBaseApplication(), this, getBaseChain(), getAccount()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new AllRewardGrpcTask(getBaseApplication(), this, getBaseChain(), getAccount()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new ReDelegationsToGrpcTask(getBaseApplication(), this, getBaseChain(), getAccount(), mValOpAddress).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void onCheckDelegate() {
        if (!getAccount().hasPrivateKey) {
            Dialog_WatchMode add = Dialog_WatchMode.newInstance();
            showDialog(add);
            return;
        }

        final WalletBalance balance = getFullBalance(getBaseChain().getMainDenom());
        BigDecimal delegatableAmount = balance.getBalanceAmount(); // TODO add(getVesting(denom))
        BigDecimal feeAmount = getBaseChain().getGasFeeEstimateCalculator().calc(getBaseChain(), CONST_PW_TX_SIMPLE_DELEGATE);
        if (delegatableAmount.compareTo(feeAmount) < 0) {
            Toast.makeText(getBaseContext(), R.string.error_not_enough_to_delegate, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mGrpcValidator.getJailed()) {
            Toast.makeText(getBaseContext(), R.string.error_disabled_jailed, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mGrpcValidator.getStatus().equals(BOND_STATUS_BONDED)) {
            Dialog_Not_Top_100 add = Dialog_Not_Top_100.newInstance(null);
            showDialog(add);
        } else {
            onStartDelegate();
        }
    }

    public void onStartDelegate() {
        Intent toDelegate = new Intent(ValidatorActivity.this, DelegateActivity.class);
        toDelegate.putExtra("valOpAddress", mValOpAddress);
        startActivity(toDelegate);
    }

    public void onCheckRedelegate() {
        if (!getAccount().hasPrivateKey) {
            Dialog_WatchMode add = Dialog_WatchMode.newInstance();
            showDialog(add);
            return;
        }

        if (mGrpcMyDelegation == null || getBaseDao().getDelegation(mValOpAddress).compareTo(BigDecimal.ZERO) <= 0) {
            Toast.makeText(getBaseContext(), R.string.error_no_redelegate, Toast.LENGTH_SHORT).show();
            return;
        }

        final String mainDenom = getBaseChain().getMainDenom();
        final WalletBalance balance = getFullBalance(mainDenom);
        BigDecimal feeAmount = getBaseChain().getGasFeeEstimateCalculator().calc(getBaseChain(), CONST_PW_TX_SIMPLE_REDELEGATE);
        if (balance.getBalanceAmount().compareTo(feeAmount) < 0) {
            Toast.makeText(getBaseContext(), R.string.error_not_enough_budget, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mGrpcRedelegates != null && mGrpcRedelegates.size() > 0) {
            for (Staking.RedelegationResponse data : mGrpcRedelegates) {
                if (data.getRedelegation().getValidatorDstAddress().equals(mValOpAddress)) {
                    Dialog_RedelegationLimited dialog = Dialog_RedelegationLimited.newInstance();
                    showDialog(dialog);
                    return;
                }
            }
        }
        onStartRedelegate();
    }

    public void onStartRedelegate() {
        Intent toDelegate = new Intent(ValidatorActivity.this, RedelegateActivity.class);
        toDelegate.putExtra("valOpAddress", mValOpAddress);
        startActivity(toDelegate);
    }

    private void onStartUndelegate() {
        if (!getAccount().hasPrivateKey) {
            Dialog_WatchMode add = Dialog_WatchMode.newInstance();
            showDialog(add);
            return;
        }
        final String mainDenom = getBaseChain().getMainDenom();
        final WalletBalance balance = getFullBalance(mainDenom);
        if (getBaseDao().getDelegation(mValOpAddress).compareTo(BigDecimal.ZERO) <= 0) {
            Toast.makeText(getBaseContext(), R.string.error_no_undelegate, Toast.LENGTH_SHORT).show();
            return;
        }
        if (getBaseDao().getUndelegationInfo(mValOpAddress) != null && getBaseDao().getUndelegationInfo(mValOpAddress).getEntriesList() != null && getBaseDao().getUndelegationInfo(mValOpAddress).getEntriesList().size() >= 7) {
            Toast.makeText(getBaseContext(), R.string.error_unbond_cnt_over, Toast.LENGTH_SHORT).show();
            return;
        }

        BigDecimal feeAmount = getBaseChain().getGasFeeEstimateCalculator().calc(getBaseChain(), CONST_PW_TX_SIMPLE_UNDELEGATE);
        if (balance.getBalanceAmount().compareTo(feeAmount) < 0) {
            Toast.makeText(getBaseContext(), R.string.error_not_enough_fee, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent toDelegate = new Intent(ValidatorActivity.this, UndelegateActivity.class);
        toDelegate.putExtra("valOpAddress", mValOpAddress);
        startActivity(toDelegate);
    }

    private void onGetReward() {
        if (!getAccount().hasPrivateKey) {
            Dialog_WatchMode add = Dialog_WatchMode.newInstance();
            showDialog(add);
            return;
        }
        final String mainDenom = getBaseChain().getMainDenom();
        final WalletBalance balance = getFullBalance(mainDenom);
        BigDecimal feeAmount = getBaseChain().getGasFeeEstimateCalculator().calc(getBaseChain(), CONST_PW_TX_SIMPLE_REWARD, 0);
        if (balance.getBalanceAmount().compareTo(feeAmount) < 0) {
            Toast.makeText(getBaseContext(), R.string.error_not_enough_fee, Toast.LENGTH_SHORT).show();
            return;
        }

        final BigDecimal reward = getBaseDao().getReward(getBaseChain().getMainDenom(), mValOpAddress);
        if (reward.compareTo(feeAmount) <= 0) {
            Toast.makeText(getBaseContext(), R.string.error_small_reward, Toast.LENGTH_SHORT).show();
            return;
        }

        if (reward.compareTo(BigDecimal.ZERO) <= 0) {
            Toast.makeText(getBaseContext(), R.string.error_not_enough_reward, Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> valAdds = new ArrayList<>();
        valAdds.add(mValOpAddress);
        Intent claimReward = new Intent(ValidatorActivity.this, ClaimRewardActivity.class);
        claimReward.putStringArrayListExtra("valOpAddresses", valAdds);
        startActivity(claimReward);
    }

    private void onCheckReInvest() {
        if (!getAccount().hasPrivateKey) {
            Dialog_WatchMode add = Dialog_WatchMode.newInstance();
            showDialog(add);
            return;
        }

        final String mainDenom = getBaseChain().getMainDenom();
        final WalletBalance balance = getFullBalance(mainDenom);
        BigDecimal feeAmount = getBaseChain().getGasFeeEstimateCalculator().calc(getBaseChain(), CONST_PW_TX_REINVEST);
        if (balance.getBalanceAmount().compareTo(feeAmount) < 0) {
            Toast.makeText(getBaseContext(), R.string.error_not_enough_budget, Toast.LENGTH_SHORT).show();
            return;
        }

        if (getBaseDao().getReward(getBaseChain().getMainDenom(), mValOpAddress).compareTo(feeAmount) < 0) {
            Toast.makeText(getBaseContext(), R.string.error_small_reward, Toast.LENGTH_SHORT).show();
            return;
        }

        new WithdrawAddressGrpcTask(getBaseApplication(), result -> {
            String rewardAddress = (String) result.resultData;
            if (rewardAddress == null || !rewardAddress.equals(getAccount().address)) {
                Toast.makeText(getBaseContext(), R.string.error_reward_address_changed_msg, Toast.LENGTH_SHORT).show();
                return;
            } else {
                Intent reinvest = new Intent(ValidatorActivity.this, ReInvestActivity.class);
                reinvest.putExtra("valOpAddress", mValOpAddress);
                startActivity(reinvest);
            }
        }, getBaseChain(), getAccount()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void onFetchValHistory() {
        mTaskCount++;
        new ApiStakeTxsHistoryTask(getBaseApplication(), this, getAccount().address, mValOpAddress, getBaseChain()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onTaskResponse(TaskResult result) {
        mTaskCount--;
        if (isFinishing()) return;
        if (result.taskType == BaseConstant.TASK_FETCH_API_STAKE_HISTORY) {
            ArrayList<ResApiNewTxListCustom> hits = (ArrayList<ResApiNewTxListCustom>) result.resultData;
            if (hits != null && hits.size() > 0) {
                mApiNewTxCustomHistory = hits;

            }
        } else if (result.taskType == TASK_GRPC_FETCH_DELEGATIONS) {
            ArrayList<Staking.DelegationResponse> delegations = (ArrayList<Staking.DelegationResponse>) result.resultData;
            if (delegations != null) {
                getBaseDao().mGrpcDelegations = delegations;
            }

        } else if (result.taskType == TASK_GRPC_FETCH_UNDELEGATIONS) {
            ArrayList<Staking.UnbondingDelegation> undelegations = (ArrayList<Staking.UnbondingDelegation>) result.resultData;
            if (undelegations != null) {
                getBaseDao().mGrpcUndelegations = undelegations;
            }

        } else if (result.taskType == TASK_GRPC_FETCH_ALL_REWARDS) {
            ArrayList<Distribution.DelegationDelegatorReward> rewards = (ArrayList<Distribution.DelegationDelegatorReward>) result.resultData;
            if (rewards != null) {
                getBaseDao().mGrpcRewards = rewards;
            }

        } else if (result.taskType == TASK_GRPC_FETCH_VALIDATOR_INFO) {
            mGrpcValidator = (Staking.Validator) result.resultData;

        } else if (result.taskType == TASK_GRPC_FETCH_SELF_BONDING) {
            mGrpcSelfDelegation = (Staking.DelegationResponse) result.resultData;

        } else if (result.taskType == TASK_GRPC_FETCH_REDELEGATIONS_TO) {
            mGrpcRedelegates = (List<Staking.RedelegationResponse>) result.resultData;
        }


        if (mTaskCount == 0) {
            mGrpcMyDelegation = getBaseDao().getDelegationInfo(mValOpAddress);
            mGrpcMyUndelegation = getBaseDao().getUndelegationInfo(mValOpAddress);

            mRecyclerView.setVisibility(View.VISIBLE);
            mValidatorAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
            hideWaitDialog();
        }
    }

    private class ValidatorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_VALIDATOR = 0;
        private static final int TYPE_MY_VALIDATOR = 1;
        private static final int TYPE_ACTION = 2;
        private static final int TYPE_HISTORY_HEADER = 3;
        private static final int TYPE_HISTORY = 4;
        private static final int TYPE_HISTORY_EMPTY = 5;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            if (viewType == TYPE_VALIDATOR) {
                return new ValidatorHolder(getLayoutInflater().inflate(R.layout.item_validator_detail, viewGroup, false));
            } else if (viewType == TYPE_MY_VALIDATOR) {
                return new MyValidatorHolder(getLayoutInflater().inflate(R.layout.item_validator_my_detail, viewGroup, false));
            } else if (viewType == TYPE_ACTION) {
                return new MyActionHolder(getLayoutInflater().inflate(R.layout.item_validator_my_action, viewGroup, false));
            } else if (viewType == TYPE_HISTORY_HEADER) {
                return new HistoryHeaderHolder(getLayoutInflater().inflate(R.layout.item_validator_history_header, viewGroup, false));
            } else if (viewType == TYPE_HISTORY) {
                return new HistoryNewHolder(getLayoutInflater().inflate(R.layout.item_new_history, viewGroup, false));
            } else if (viewType == TYPE_HISTORY_EMPTY) {
                return new HistoryEmptyHolder(getLayoutInflater().inflate(R.layout.item_validator_history_empty, viewGroup, false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            if (mGrpcValidator == null) return;
            if (getItemViewType(position) == TYPE_VALIDATOR) {
                onBindValidatorV1(viewHolder);

            } else if (getItemViewType(position) == TYPE_MY_VALIDATOR) {
                onBindMyValidatorV1(viewHolder);

            } else if (getItemViewType(position) == TYPE_ACTION) {
                onBindActionV1(viewHolder);

            }
            if (getItemViewType(position) == TYPE_HISTORY) {
                onBindApiHistory(viewHolder, position);
            }

        }

        private void onBindApiHistory(RecyclerView.ViewHolder viewHolder, int position) {
            final HistoryNewHolder holder = (HistoryNewHolder) viewHolder;
            final ResApiNewTxListCustom history;
            if (getBaseChain().isGRPC()) {
                if (mGrpcMyDelegation == null && mGrpcMyUndelegation == null) {
                    history = mApiNewTxCustomHistory.get(position - 2);
                } else {
                    history = mApiNewTxCustomHistory.get(position - 3);
                }

            } else {
                history = mApiNewTxCustomHistory.get(position - 3);
            }
            holder.historyType.setText(history.getMsgType(getBaseContext(), getAccount().address));
            holder.history_time.setText(WDp.getTimeTxformat(getBaseContext(), history.data.timestamp));
            holder.history_time_gap.setText(WDp.getTimeTxGap(getBaseContext(), history.data.timestamp));
            final Coin coin = history.getDpCoin(getBaseChain());
            if (coin != null) {
                holder.history_amount_symbol.setVisibility(View.VISIBLE);
                holder.history_amount.setVisibility(View.VISIBLE);
                WDp.showCoinDp(getBaseDao(), history.getDpCoin(getBaseChain()).denom, history.getDpCoin(getBaseChain()).amount, holder.history_amount_symbol, holder.history_amount, getBaseChain());
            } else if (history.getMsgType(ValidatorActivity.this, getAccount().address).equals(getString(R.string.tx_vote))) {
                holder.history_amount_symbol.setVisibility(View.VISIBLE);
                holder.history_amount_symbol.setText(history.getVoteOption());
                holder.history_amount_symbol.setTextColor(ContextCompat.getColor(ValidatorActivity.this, R.color.colorWhite));
                holder.history_amount.setVisibility(View.GONE);
            } else {
                holder.history_amount_symbol.setVisibility(View.GONE);
                holder.history_amount.setVisibility(View.GONE);
            }
            if (history.isSuccess()) {
                holder.historySuccess.setVisibility(View.GONE);
            } else {
                holder.historySuccess.setVisibility(View.VISIBLE);
            }
            holder.historyRoot.setOnClickListener(v -> {
                if (history.data.txhash != null) {
                    if (getBaseChain().isGRPC()) {
                        String url = WUtil.getTxExplorer(baseChain, history.data.txhash);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } else {
                        if (!TextUtils.isEmpty(history.header.chain_id) && !getBaseDao().getChainId().equals(history.header.chain_id)) {
                            String url = WUtil.getTxExplorer(getBaseChain(), history.data.txhash);
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);

                        } else {
                            Intent txDetail = new Intent(getBaseContext(), TxDetailActivity.class);
                            txDetail.putExtra("txHash", history.data.txhash);
                            txDetail.putExtra("isGen", false);
                            txDetail.putExtra("isSuccess", true);
                            startActivity(txDetail);
                        }
                    }
                }
            });
        }

        private void onBindValidatorV1(RecyclerView.ViewHolder viewHolder) {
            final ValidatorHolder holder = (ValidatorHolder) viewHolder;
            final int dpDecimal = getBaseChain().getDivideDecimal();
            holder.itemTvMoniker.setText(mGrpcValidator.getDescription().getMoniker());
            holder.itemTvAddress.setText(mGrpcValidator.getOperatorAddress());
            holder.itemBandOracleOff.setVisibility(View.INVISIBLE);
            if (!TextUtils.isEmpty(mGrpcValidator.getDescription().getWebsite())) {
                holder.itemTvWebsite.setText(mGrpcValidator.getDescription().getWebsite());
            } else {
                holder.itemTvWebsite.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(mGrpcValidator.getDescription().getDetails())) {
                holder.itemTvDescription.setText(mGrpcValidator.getDescription().getDetails());
            } else {
                holder.itemTvDescription.setVisibility(View.GONE);
            }
            if (mGrpcSelfDelegation != null) {
                holder.itemTvSelfBondRate.setText(WDp.getSelfBondRate(mGrpcValidator.getTokens(), mGrpcSelfDelegation.getBalance().getAmount()));
            } else {
                holder.itemTvSelfBondRate.setText(WDp.getPercentDp(BigDecimal.ZERO));
            }

            if (mGrpcValidator.getJailed()) {
                holder.itemAvatar.setBorderColor(ContextCompat.getColor(ValidatorActivity.this, R.color.colorRed));
                holder.itemImgRevoked.setVisibility(View.VISIBLE);
            } else {
                holder.itemAvatar.setBorderColor(ContextCompat.getColor(ValidatorActivity.this, R.color.colorGray3));
                holder.itemImgRevoked.setVisibility(View.GONE);
            }

            if (getBaseChain().equals(BAND_MAIN.INSTANCE)) {
                if (getBaseDao().mChainParam != null && !getBaseDao().mChainParam.isOracleEnable(mGrpcValidator.getOperatorAddress())) {
                    holder.itemBandOracleOff.setImageDrawable(getDrawable(R.drawable.band_oracleoff_l));
                    holder.itemTvYieldRate.setTextColor(ContextCompat.getColor(ValidatorActivity.this, R.color.colorRed));
                } else {
                    holder.itemBandOracleOff.setImageDrawable(getDrawable(R.drawable.band_oracleon_l));
                }
                holder.itemBandOracleOff.setVisibility(View.VISIBLE);
            }

            holder.itemTvCommissionRate.setText(WDp.getDpCommissionGrpcRate(mGrpcValidator));
            holder.itemTvTotalBondAmount.setText(WDp.getDpAmount2(new BigDecimal(mGrpcValidator.getTokens()), dpDecimal, dpDecimal));
            if (mGrpcValidator.getStatus().equals(BOND_STATUS_BONDED)) {
                holder.itemTvYieldRate.setText(WDp.getDpEstAprCommission(getBaseDao(), getBaseChain(), new BigDecimal(mGrpcValidator.getCommission().getCommissionRates().getRate()).movePointLeft(18)));
            } else {
                holder.itemTvYieldRate.setText(WDp.getDpEstAprCommission(getBaseDao(), getBaseChain(), BigDecimal.ONE));
                holder.itemTvYieldRate.setTextColor(ContextCompat.getColor(ValidatorActivity.this, R.color.colorRed));
            }
            try {
                Picasso.get().load(getBaseChain().getMonikerImageLink(mValOpAddress)).fit().placeholder(R.drawable.validator_none_img).error(R.drawable.validator_none_img).into(holder.itemAvatar);
            } catch (Exception e) {
            }

            if (getBaseChain().equals(ALTHEA_TEST.INSTANCE)) {
                holder.itemTvYieldRate.setText("--");
            }


            holder.itemBtnDelegate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCheckDelegate();
                }
            });
        }

        private void onBindMyValidatorV1(RecyclerView.ViewHolder viewHolder) {
            final MyValidatorHolder holder = (MyValidatorHolder) viewHolder;
            final int dpDecimal = getBaseChain().getDivideDecimal();
            holder.itemTvMoniker.setText(mGrpcValidator.getDescription().getMoniker());
            holder.itemTvAddress.setText(mGrpcValidator.getOperatorAddress());
            holder.itemBandOracleOff.setVisibility(View.INVISIBLE);
            if (!TextUtils.isEmpty(mGrpcValidator.getDescription().getWebsite())) {
                holder.itemTvWebsite.setText(mGrpcValidator.getDescription().getWebsite());
            } else {
                holder.itemTvWebsite.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(mGrpcValidator.getDescription().getDetails())) {
                holder.itemTvDescription.setText(mGrpcValidator.getDescription().getDetails());
            } else {
                holder.itemTvDescription.setVisibility(View.GONE);
            }

            if (mGrpcSelfDelegation != null) {
                holder.itemTvSelfBondRate.setText(WDp.getSelfBondRate(mGrpcValidator.getTokens(), mGrpcSelfDelegation.getBalance().getAmount()));
            } else {
                holder.itemTvSelfBondRate.setText(WDp.getPercentDp(BigDecimal.ZERO));
            }
            if (mGrpcValidator.getJailed()) {
                holder.itemAvatar.setBorderColor(ContextCompat.getColor(ValidatorActivity.this, R.color.colorRed));
                holder.itemImgRevoked.setVisibility(View.VISIBLE);
            } else {
                holder.itemAvatar.setBorderColor(ContextCompat.getColor(ValidatorActivity.this, R.color.colorGray3));
                holder.itemImgRevoked.setVisibility(View.GONE);
            }

            if (getBaseChain().equals(BAND_MAIN.INSTANCE)) {
                if (getBaseDao().mChainParam != null && !getBaseDao().mChainParam.isOracleEnable(mGrpcValidator.getOperatorAddress())) {
                    holder.itemBandOracleOff.setImageDrawable(getDrawable(R.drawable.band_oracleoff_l));
                    holder.itemTvYieldRate.setTextColor(ContextCompat.getColor(ValidatorActivity.this, R.color.colorRed));
                } else {
                    holder.itemBandOracleOff.setImageDrawable(getDrawable(R.drawable.band_oracleon_l));
                }
                holder.itemBandOracleOff.setVisibility(View.VISIBLE);
            }

            holder.itemTvCommissionRate.setText(WDp.getDpCommissionGrpcRate(mGrpcValidator));
            holder.itemTvTotalBondAmount.setText(WDp.getDpAmount2(new BigDecimal(mGrpcValidator.getTokens()), dpDecimal, dpDecimal));
            if (mGrpcValidator.getStatus().equals(BOND_STATUS_BONDED)) {
                holder.itemTvYieldRate.setText(WDp.getDpEstAprCommission(getBaseDao(), getBaseChain(), new BigDecimal(mGrpcValidator.getCommission().getCommissionRates().getRate()).movePointLeft(18)));
            } else {
                holder.itemTvYieldRate.setText(WDp.getDpEstAprCommission(getBaseDao(), getBaseChain(), BigDecimal.ONE));
                holder.itemTvYieldRate.setTextColor(ContextCompat.getColor(ValidatorActivity.this, R.color.colorRed));
            }
            try {
                Picasso.get().load(getBaseChain().getMonikerImageLink(mValOpAddress)).fit().placeholder(R.drawable.validator_none_img).error(R.drawable.validator_none_img).into(holder.itemAvatar);
            } catch (Exception e) {
            }

            if (getBaseChain().equals(ALTHEA_TEST.INSTANCE)) {
                holder.itemTvYieldRate.setText("--");
            }
        }

        private void onBindActionV1(RecyclerView.ViewHolder viewHolder) {
            final MyActionHolder holder = (MyActionHolder) viewHolder;
            final int dpDecimal = getBaseChain().getDivideDecimal();
            holder.itemRoot.setCardBackgroundColor(WDp.getChainBgColor(getBaseContext(), getBaseChain()));
            holder.itemTvDelegatedAmount.setText(WDp.getDpAmount2(getBaseDao().getDelegation(mValOpAddress), dpDecimal, dpDecimal));
            holder.itemTvUnbondingAmount.setText(WDp.getDpAmount2(getBaseDao().getUndelegation(mValOpAddress), dpDecimal, dpDecimal));
            holder.itemTvSimpleReward.setText(WDp.getDpAmount2(getBaseDao().getReward(getBaseChain().getMainDenom(), mValOpAddress), dpDecimal, dpDecimal));

            if (!mGrpcValidator.getStatus().equals(BOND_STATUS_BONDED) || mGrpcMyDelegation == null) {
                holder.itemDailyReturn.setText(WDp.getDailyReward(getBaseContext(), getBaseDao(), BigDecimal.ONE, BigDecimal.ONE, getBaseChain()));
                holder.itemMonthlyReturn.setText(WDp.getMonthlyReward(getBaseContext(), getBaseDao(), BigDecimal.ONE, BigDecimal.ONE, getBaseChain()));
                if (!mGrpcValidator.getStatus().equals(BOND_STATUS_BONDED)) {
                    holder.itemDailyReturn.setTextColor(ContextCompat.getColor(ValidatorActivity.this, R.color.colorRed));
                    holder.itemMonthlyReturn.setTextColor(ContextCompat.getColor(ValidatorActivity.this, R.color.colorRed));
                }
            } else {
                holder.itemDailyReturn.setText(WDp.getDailyReward(getBaseContext(), getBaseDao(), WDp.getCommissionGrpcRate(mGrpcValidator), getBaseDao().getDelegation(mValOpAddress), getBaseChain()));
                holder.itemMonthlyReturn.setText(WDp.getMonthlyReward(getBaseContext(), getBaseDao(), WDp.getCommissionGrpcRate(mGrpcValidator), getBaseDao().getDelegation(mValOpAddress), getBaseChain()));

            }

            if (getBaseChain().equals(ALTHEA_TEST.INSTANCE)) {
                holder.itemDailyReturn.setText("--");
                holder.itemMonthlyReturn.setText("--");
            }

            holder.itemBtnDelegate.setOnClickListener(v -> onCheckDelegate());
            holder.itemBtnUndelegate.setOnClickListener(v -> onStartUndelegate());
            holder.itemBtnRedelegate.setOnClickListener(v -> onCheckRedelegate());
            holder.itemBtnReward.setOnClickListener(v -> onGetReward());
            holder.itemBtnReinvest.setOnClickListener(v -> onCheckReInvest());

        }

        @Override
        public int getItemViewType(int position) {
            if (mGrpcMyDelegation == null && mGrpcMyUndelegation == null) {
                if (position == 0) {
                    return TYPE_VALIDATOR;
                } else if (position == 1) {
                    return TYPE_HISTORY_HEADER;
                }

            } else {
                if (position == 0) {
                    return TYPE_MY_VALIDATOR;
                } else if (position == 1) {
                    return TYPE_ACTION;
                } else if (position == 2) {
                    return TYPE_HISTORY_HEADER;
                }
            }
            if (mApiNewTxCustomHistory.size() > 0) {
                return TYPE_HISTORY;
            }
            return TYPE_HISTORY_EMPTY;
        }

        @Override
        public int getItemCount() {
            if (mGrpcMyDelegation == null && mGrpcMyUndelegation == null) {
                if (mApiNewTxCustomHistory.size() > 0) {
                    return mApiNewTxCustomHistory.size() + 2;
                } else {
                    return 3;
                }
            } else {
                if (mApiNewTxCustomHistory.size() > 0) {
                    return mApiNewTxCustomHistory.size() + 3;
                } else {
                    return 4;
                }
            }
        }

        public class ValidatorHolder extends RecyclerView.ViewHolder {
            CircleImageView itemAvatar;
            ImageView itemImgRevoked;
            ImageView itemImgFree;
            ImageView itemBandOracleOff;
            TextView itemTvMoniker;
            TextView itemTvAddress;
            TextView itemTvWebsite;
            TextView itemTvDescription;
            TextView itemTvTotalBondAmount;
            TextView itemTvSelfBondRate;
            TextView itemTvYieldRate;
            TextView itemTvCommissionRate;
            Button itemBtnDelegate;

            public ValidatorHolder(View v) {
                super(v);
                itemAvatar = itemView.findViewById(R.id.validator_avatar);
                itemImgRevoked = itemView.findViewById(R.id.avatar_validator_revoke);
                itemImgFree = itemView.findViewById(R.id.avatar_validator_free);
                itemTvMoniker = itemView.findViewById(R.id.validator_moniker);
                itemBandOracleOff = itemView.findViewById(R.id.band_oracle_off);
                itemTvAddress = itemView.findViewById(R.id.validator_address);
                itemTvWebsite = itemView.findViewById(R.id.validator_site);
                itemTvDescription = itemView.findViewById(R.id.validator_description);
                itemTvTotalBondAmount = itemView.findViewById(R.id.validator_total_bonded);
                itemTvSelfBondRate = itemView.findViewById(R.id.validator_self_bond_rate);
                itemTvYieldRate = itemView.findViewById(R.id.validator_yield);
                itemTvCommissionRate = itemView.findViewById(R.id.validator_commission);
                itemBtnDelegate = itemView.findViewById(R.id.validator_btn_delegate);
            }
        }

        public class MyValidatorHolder extends RecyclerView.ViewHolder {
            CardView itemRoot;
            CircleImageView itemAvatar;
            ImageView itemImgRevoked;
            ImageView itemImgFree;
            ImageView itemBandOracleOff;
            TextView itemTvMoniker, itemTvAddress, itemTvWebsite, itemTvDescription, itemTvTotalBondAmount,
                    itemTvYieldRate, itemTvSelfBondRate, itemTvCommissionRate;

            public MyValidatorHolder(View v) {
                super(v);
                itemRoot = itemView.findViewById(R.id.root);
                itemAvatar = itemView.findViewById(R.id.validator_avatar);
                itemImgRevoked = itemView.findViewById(R.id.avatar_validator_revoke);
                itemImgFree = itemView.findViewById(R.id.avatar_validator_free);
                itemTvMoniker = itemView.findViewById(R.id.validator_moniker);
                itemBandOracleOff = itemView.findViewById(R.id.band_oracle_off);
                itemTvAddress = itemView.findViewById(R.id.validator_address);
                itemTvWebsite = itemView.findViewById(R.id.validator_site);
                itemTvDescription = itemView.findViewById(R.id.validator_description);
                itemTvTotalBondAmount = itemView.findViewById(R.id.validator_total_bonded);
                itemTvSelfBondRate = itemView.findViewById(R.id.validator_self_bond_rate);
                itemTvYieldRate = itemView.findViewById(R.id.validator_yield);
                itemTvCommissionRate = itemView.findViewById(R.id.validator_commission);
            }
        }

        public class MyActionHolder extends RecyclerView.ViewHolder {
            CardView itemRoot;
            TextView itemTvDelegatedAmount, itemTvUnbondingAmount, itemTvAtomReward, itemTvPhotonReward, itemTvSimpleReward;
            Button itemBtnDelegate, itemBtnUndelegate, itemBtnRedelegate, itemBtnReward, itemBtnReinvest;
            TextView itemAtomTitle, itemPhotonTitle;
            RelativeLayout itemAtomLayer, itemPhotonLayer;
            TextView itemDailyReturn, itemMonthlyReturn;

            public MyActionHolder(View v) {
                super(v);
                itemRoot = itemView.findViewById(R.id.root);
                itemTvDelegatedAmount = itemView.findViewById(R.id.validator_delegated);
                itemTvUnbondingAmount = itemView.findViewById(R.id.validator_unbonding);
                itemTvAtomReward = itemView.findViewById(R.id.validator_atom_reward);
                itemTvPhotonReward = itemView.findViewById(R.id.validator_photon_reward);
                itemBtnDelegate = itemView.findViewById(R.id.validator_btn_delegate);
                itemBtnUndelegate = itemView.findViewById(R.id.validator_btn_undelegate);
                itemBtnRedelegate = itemView.findViewById(R.id.validator_btn_redelegate);
                itemBtnReward = itemView.findViewById(R.id.validator_btn_claim_reward);
                itemBtnReinvest = itemView.findViewById(R.id.validator_btn_reinvest);
                itemAtomTitle = itemView.findViewById(R.id.action_atom_title);
                itemPhotonTitle = itemView.findViewById(R.id.action_photon_title);
                itemPhotonLayer = itemView.findViewById(R.id.validator_photon_reward_layer);
                itemAtomLayer = itemView.findViewById(R.id.validator_atom_reward_layer);
                itemTvSimpleReward = itemView.findViewById(R.id.validator_simple_reward);
                itemDailyReturn = itemView.findViewById(R.id.validator_daily_return);
                itemMonthlyReturn = itemView.findViewById(R.id.validator_monthly_return);
            }
        }

        public class HistoryHeaderHolder extends RecyclerView.ViewHolder {

            public HistoryHeaderHolder(View v) {
                super(v);
            }
        }

        public class HistoryNewHolder extends RecyclerView.ViewHolder {
            private final CardView historyRoot;
            private final TextView historyType;
            private final TextView historySuccess;
            private final TextView history_time;
            private final TextView history_amount;
            private final TextView history_amount_symbol;
            private final TextView history_time_gap;

            public HistoryNewHolder(View v) {
                super(v);
                historyRoot = itemView.findViewById(R.id.card_history);
                historyType = itemView.findViewById(R.id.history_type);
                historySuccess = itemView.findViewById(R.id.history_success);
                history_time = itemView.findViewById(R.id.history_time);
                history_time_gap = itemView.findViewById(R.id.history_time_gap);
                history_amount = itemView.findViewById(R.id.history_amount);
                history_amount_symbol = itemView.findViewById(R.id.history_amount_symobl);
            }
        }

        public class HistoryEmptyHolder extends RecyclerView.ViewHolder {

            public HistoryEmptyHolder(View v) {
                super(v);
            }
        }

    }

}
