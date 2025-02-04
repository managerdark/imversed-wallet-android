package wannabit.io.cosmostaion.activities;

import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIMPLE_DELEGATE;
import static wannabit.io.cosmostaion.base.BaseConstant.CONST_PW_TX_SIMPLE_REWARD;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.fulldive.wallet.models.WalletBalance;
import com.google.android.material.tabs.TabLayout;

import java.math.BigDecimal;
import java.util.ArrayList;

import cosmos.distribution.v1beta1.Distribution;
import cosmos.staking.v1beta1.Staking;
import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.base.BaseActivity;
import wannabit.io.cosmostaion.base.BaseFragment;
import wannabit.io.cosmostaion.base.IBusyFetchListener;
import wannabit.io.cosmostaion.base.IRefreshTabListener;
import wannabit.io.cosmostaion.dialog.Dialog_WatchMode;
import wannabit.io.cosmostaion.fragment.ValidatorAllFragment;
import wannabit.io.cosmostaion.fragment.ValidatorMyFragment;
import wannabit.io.cosmostaion.fragment.ValidatorOtherFragment;
import wannabit.io.cosmostaion.model.type.Validator;
import wannabit.io.cosmostaion.utils.FetchCallBack;
import wannabit.io.cosmostaion.utils.WDp;
import wannabit.io.cosmostaion.utils.WUtil;

public class ValidatorListActivity extends BaseActivity implements FetchCallBack {

    private ValidatorPageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validator_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TabLayout validatorTapLayer = findViewById(R.id.validator_tab);
        ViewPager validatorPager = findViewById(R.id.validator_view_pager);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pageAdapter = new ValidatorPageAdapter(getSupportFragmentManager());
        validatorPager.setAdapter(pageAdapter);
        validatorTapLayer.setupWithViewPager(validatorPager);
        validatorTapLayer.setTabRippleColor(null);

        View tab0 = LayoutInflater.from(this).inflate(R.layout.view_tab_myvalidator, null);
        TextView tabItemText0 = tab0.findViewById(R.id.tabItemText);
        tabItemText0.setText(R.string.str_my_validators);
        tabItemText0.setTextColor(WDp.getTabColor(this, getBaseChain()));
        validatorTapLayer.getTabAt(0).setCustomView(tab0);

        View tab1 = LayoutInflater.from(this).inflate(R.layout.view_tab_myvalidator, null);
        TextView tabItemText1 = tab1.findViewById(R.id.tabItemText);
        tabItemText1.setTextColor(WDp.getTabColor(this, getBaseChain()));
        tabItemText1.setText(R.string.str_top_100_validators);
        validatorTapLayer.getTabAt(1).setCustomView(tab1);

        View tab2 = LayoutInflater.from(this).inflate(R.layout.view_tab_myvalidator, null);
        TextView tabItemText2 = tab2.findViewById(R.id.tabItemText);
        tabItemText2.setTextColor(WDp.getTabColor(this, getBaseChain()));
        tabItemText2.setText(R.string.str_other_validators);
        validatorTapLayer.getTabAt(2).setCustomView(tab2);

        validatorTapLayer.setTabIconTint(WDp.getChainTintColor(this, getBaseChain()));
        validatorTapLayer.setSelectedTabIndicatorColor(WDp.getChainColor(this, getBaseChain()));

        validatorPager.setOffscreenPageLimit(3);
        validatorPager.setCurrentItem(0, false);

        validatorPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }

            @Override
            public void onPageSelected(int i) {
                Fragment fragment = pageAdapter.mFragments.get(i);
                if (fragment instanceof IRefreshTabListener) {
                    ((IRefreshTabListener) fragment).onRefreshTab();
                }
            }
        });
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

    public void onStartValidatorDetail(Validator validator) {
        Intent intent = new Intent(ValidatorListActivity.this, ValidatorActivity.class);
        intent.putExtra("validator", validator);
        startActivity(intent);
    }

    public void onStartValidatorDetailV1(String opAddress) {
        Intent intent = new Intent(ValidatorListActivity.this, ValidatorActivity.class);
        intent.putExtra("valOpAddress", opAddress);
        startActivity(intent);
    }

    public void onStartDelegate() {
        if (!getAccount().hasPrivateKey) {
            Dialog_WatchMode dialog = Dialog_WatchMode.newInstance();
            showDialog(dialog);
            return;
        }
        if (getBaseChain().isGRPC()) {
            String cosmostation = "";
            final WalletBalance balance = getFullBalance(getBaseChain().getMainDenom());
            BigDecimal delegatableAmount = balance.getBalanceAmount(); // TODO add(getVesting(denom))
            BigDecimal feeAmount = getBaseChain().getGasFeeEstimateCalculator().calc(getBaseChain(), CONST_PW_TX_SIMPLE_DELEGATE);
            if (delegatableAmount.compareTo(feeAmount) < 0) {
                Toast.makeText(getBaseContext(), R.string.error_not_enough_to_delegate, Toast.LENGTH_SHORT).show();
                return;
            }
            for (Staking.Validator validator : getBaseDao().mGRpcAllValidators) {
                if (validator.getDescription().getMoniker().equalsIgnoreCase("Cosmostation")) {
                    cosmostation = validator.getOperatorAddress();
                }
            }
            if (!cosmostation.isEmpty()) {
                Intent toDelegate = new Intent(ValidatorListActivity.this, DelegateActivity.class);
                toDelegate.putExtra("valOpAddress", cosmostation);
                startActivity(toDelegate);
            }
        } else {
            Validator toValidator = null;
            final WalletBalance balance = getFullBalance(getBaseChain().getMainDenom());
            BigDecimal delegatableAmount = balance.getDelegatableAmount();
            BigDecimal feeAmount = getBaseChain().getGasFeeEstimateCalculator().calc(getBaseChain(), CONST_PW_TX_SIMPLE_DELEGATE);
            if (delegatableAmount.compareTo(feeAmount) < 0) {
                Toast.makeText(getBaseContext(), R.string.error_not_enough_to_delegate, Toast.LENGTH_SHORT).show();
                return;
            }
            for (Validator validator : getBaseDao().mAllValidators) {
                if (validator.description.moniker.equalsIgnoreCase("Cosmostation")) {
                    toValidator = validator;
                }
            }
            if (toValidator != null) {
                Intent toDelegate = new Intent(ValidatorListActivity.this, DelegateActivity.class);
                toDelegate.putExtra("validator", toValidator);
                startActivity(toDelegate);
            }
        }
    }

    public void onStartRewardAll() {
        if (!getAccount().hasPrivateKey) {
            Dialog_WatchMode add = Dialog_WatchMode.newInstance();
            showDialog(add);
            return;
        }

        if (getBaseChain().isGRPC()) {
            ArrayList<String> toClaimValaddr = new ArrayList<>();
            ArrayList<Distribution.DelegationDelegatorReward> toClaimRewards = new ArrayList<>();
            if (getBaseDao().mGrpcRewards == null) {
                Toast.makeText(getBaseContext(), R.string.error_not_enough_reward, Toast.LENGTH_SHORT).show();
                return;
            }

            for (Distribution.DelegationDelegatorReward reward : getBaseDao().mGrpcRewards) {
                if (getBaseDao().getReward(getBaseChain().getMainDenom(), reward.getValidatorAddress()).compareTo(BigDecimal.ZERO) > 0) {
                    toClaimRewards.add(reward);
                }
            }
            if (toClaimRewards.size() == 0) {
                Toast.makeText(getBaseContext(), R.string.error_not_enough_reward, Toast.LENGTH_SHORT).show();
                return;
            }
            WUtil.onSortRewardAmount(toClaimRewards, getBaseChain().getMainDenom());
            if (toClaimRewards.size() >= 17) {
                ArrayList<Distribution.DelegationDelegatorReward> temp = new ArrayList(toClaimRewards.subList(0, 16));
                toClaimRewards = temp;
                Toast.makeText(getBaseContext(), R.string.str_multi_reward_max_16, Toast.LENGTH_SHORT).show();
            }

            final String mainDenom = getBaseChain().getMainDenom();
            final WalletBalance balance = getFullBalance(mainDenom);
            BigDecimal feeAmount = getBaseChain().getGasFeeEstimateCalculator().calc(getBaseChain(), CONST_PW_TX_SIMPLE_REWARD, toClaimRewards.size() - 1);
            if (balance.getBalanceAmount().compareTo(feeAmount) < 0) {
                Toast.makeText(getBaseContext(), R.string.error_not_enough_fee, Toast.LENGTH_SHORT).show();
                return;
            }
            for (Distribution.DelegationDelegatorReward reward : toClaimRewards) {
                toClaimValaddr.add(reward.getValidatorAddress());
            }

            Intent claimReward = new Intent(ValidatorListActivity.this, ClaimRewardActivity.class);
            claimReward.putStringArrayListExtra("valOpAddresses", toClaimValaddr);
            startActivity(claimReward);

        } else {
            ArrayList<Validator> toClaimValidators = new ArrayList<>();
            if (getBaseDao().rewardAmount(getBaseChain().getMainDenom()).compareTo(BigDecimal.ZERO) <= 0) {
                Toast.makeText(getBaseContext(), R.string.error_not_enough_reward, Toast.LENGTH_SHORT).show();
                return;
            }

            BigDecimal singlefeeAmount = getBaseChain().getGasFeeEstimateCalculator().calc(getBaseChain(), CONST_PW_TX_SIMPLE_REWARD);
            for (Validator validator : getBaseDao().mAllValidators) {
                if (getBaseDao().rewardAmountByValidator(getBaseChain().getMainDenom(), validator.operator_address).compareTo(singlefeeAmount) > 0) {
                    toClaimValidators.add(validator);
                }
            }

            if (toClaimValidators.size() == 0) {
                Toast.makeText(getBaseContext(), R.string.error_not_enough_reward, Toast.LENGTH_SHORT).show();
                return;
            }

            toClaimValidators = new ArrayList<>(WUtil.onSortByOnlyReward(toClaimValidators, getBaseChain().getMainDenom(), getBaseDao()));
            if (toClaimValidators.size() >= 17) {
                toClaimValidators = new ArrayList<>(getBaseDao().getMyValidators().subList(0, 16));
                Toast.makeText(getBaseContext(), R.string.str_multi_reward_max_16, Toast.LENGTH_SHORT).show();
            }

            BigDecimal available = getAccount().getTokenBalance(getBaseChain().getMainDenom());
            BigDecimal feeAmount = getBaseChain().getGasFeeEstimateCalculator().calc(getBaseChain(), CONST_PW_TX_SIMPLE_REWARD, toClaimValidators.size() - 1);

            if (available.compareTo(feeAmount) < 0) {
                Toast.makeText(getBaseContext(), R.string.error_not_enough_fee, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent claimReward = new Intent(ValidatorListActivity.this, ClaimRewardActivity.class);
            claimReward.putExtra("opAddresses", toClaimValidators);
            startActivity(claimReward);

        }

    }

    public void onFetchAllData() {
        fetchAllData(this);
    }

    @Override
    public void fetchFinished() {
        if (!isFinishing()) {
            hideWaitDialog();
            ((IRefreshTabListener) pageAdapter.mCurrentFragment).onRefreshTab();
        }

    }

    @Override
    public void fetchBusy() {
        if (!isFinishing()) {
            hideWaitDialog();
            Fragment fragment = pageAdapter.mCurrentFragment;
            if (fragment instanceof IBusyFetchListener) {
                ((IBusyFetchListener) pageAdapter.mCurrentFragment).onBusyFetch();
            }
        }

    }

    private class ValidatorPageAdapter extends FragmentPagerAdapter {

        private final ArrayList<BaseFragment> mFragments = new ArrayList<>();
        private BaseFragment mCurrentFragment;

        public ValidatorPageAdapter(FragmentManager fm) {
            super(fm);
            mFragments.clear();
            mFragments.add(ValidatorMyFragment.newInstance(null));
            mFragments.add(ValidatorAllFragment.newInstance(null));
            mFragments.add(ValidatorOtherFragment.newInstance(null));
        }

        @Override
        public BaseFragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((BaseFragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }

        public BaseFragment getCurrentFragment() {
            return mCurrentFragment;
        }

        public ArrayList<BaseFragment> getFragments() {
            return mFragments;
        }
    }
}
