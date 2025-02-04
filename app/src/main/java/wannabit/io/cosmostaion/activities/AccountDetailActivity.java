package wannabit.io.cosmostaion.activities;

import static com.fulldive.wallet.models.BaseChain.OKEX_MAIN;
import static wannabit.io.cosmostaion.base.BaseConstant.TASK_FETCH_NODE_INFO;
import static wannabit.io.cosmostaion.base.BaseConstant.TASK_GRPC_FETCH_NODE_INFO;
import static wannabit.io.cosmostaion.base.BaseConstant.TASK_GRPC_FETCH_WITHDRAW_ADDRESS;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import com.fulldive.wallet.models.BaseChain;
import com.fulldive.wallet.presentation.accounts.AccountShowDialogFragment;
import com.fulldive.wallet.presentation.accounts.DeleteConfirmDialogFragment;
import com.fulldive.wallet.presentation.accounts.restore.MnemonicRestoreActivity;
import com.fulldive.wallet.presentation.accounts.restore.PrivateKeyRestoreActivity;
import com.fulldive.wallet.presentation.main.intro.IntroActivity;
import com.fulldive.wallet.presentation.security.key.ShowPrivateKeyActivity;
import com.fulldive.wallet.presentation.security.mnemonic.ShowMnemonicActivity;
import com.fulldive.wallet.presentation.security.password.CheckPasswordActivity;
import com.fulldive.wallet.rx.AppSchedulers;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.base.BaseActivity;
import wannabit.io.cosmostaion.dao.Account;
import wannabit.io.cosmostaion.dialog.Dialog_ChangeNickName;
import wannabit.io.cosmostaion.dialog.Dialog_RewardAddressChangeInfo;
import wannabit.io.cosmostaion.dialog.Dialog_WatchMode;
import wannabit.io.cosmostaion.model.NodeInfo;
import wannabit.io.cosmostaion.task.FetchTask.NodeInfoTask;
import wannabit.io.cosmostaion.task.TaskListener;
import wannabit.io.cosmostaion.task.TaskResult;
import wannabit.io.cosmostaion.task.gRpcTask.NodeInfoGrpcTask;
import wannabit.io.cosmostaion.task.gRpcTask.WithdrawAddressGrpcTask;
import wannabit.io.cosmostaion.utils.WDp;
import wannabit.io.cosmostaion.utils.WLog;

public class AccountDetailActivity extends BaseActivity implements View.OnClickListener, TaskListener {

    private View mView;
    private Button mBtnCheck, mBtnCheckKey, mBtnDelete;

    private CardView mCardName;
    private ImageView mChainImg, mNameEditImg;
    private TextView mAccountName;

    private CardView mCardBody;
    private ImageView mBtnQr;
    private TextView mAccountAddress, mAccountGenTime;
    private TextView mAccountChain, mAccountState, mAccountPathTitle, mAccountPath, mImportMsg;
    private RelativeLayout mPathLayer;

    private CardView mCardRewardAddress;
    private ImageView mBtnRewardAddressChange;
    private TextView mRewardAddress;

    protected Account account = null;
    protected BaseChain baseChain;

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    actionDeleteAccount(account.id);
                }
            }
    );

    private final ActivityResultLauncher<Intent> launcherCheckMnemonic = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    actionCheckMnemonic(account.id);
                }
            }
    );

    private final ActivityResultLauncher<Intent> launcherCheckProvateKey = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    actionCheckPrivateKey(account.id);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        mBtnCheck = findViewById(R.id.btn_check);
        mView = findViewById(R.id.view);
        mBtnCheckKey = findViewById(R.id.btn_check_key);
        mBtnDelete = findViewById(R.id.btn_delete);
        mCardName = findViewById(R.id.card_name);
        mChainImg = findViewById(R.id.chainImageView);
        mNameEditImg = findViewById(R.id.account_edit);
        mAccountName = findViewById(R.id.account_name);
        mCardBody = findViewById(R.id.card_body);
        mBtnQr = findViewById(R.id.account_qr);
        mAccountAddress = findViewById(R.id.account_address);
        mAccountChain = findViewById(R.id.account_chain);
        mAccountGenTime = findViewById(R.id.account_import_time);
        mAccountState = findViewById(R.id.account_import_state);
        mAccountPathTitle = findViewById(R.id.path_title);
        mAccountPath = findViewById(R.id.account_path);
        mImportMsg = findViewById(R.id.import_msg);
        mPathLayer = findViewById(R.id.account_path_layer);
        mCardRewardAddress = findViewById(R.id.card_reward_address);
        mBtnRewardAddressChange = findViewById(R.id.reward_change_btn);
        mRewardAddress = findViewById(R.id.reward_address);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBtnCheck.setOnClickListener(this);
        mBtnCheckKey.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mNameEditImg.setOnClickListener(this);
        mBtnQr.setOnClickListener(this);
        mBtnRewardAddressChange.setOnClickListener(this);
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

    @Override
    protected void onResume() {
        super.onResume();
        onInitView();
    }

    public void onStartDeleteUser(Long accountId) {
        if (account.hasPrivateKey) {
            final Intent intent = new Intent(this, CheckPasswordActivity.class);
            launcher.launch(
                    intent,
                    ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_bottom, R.anim.fade_out)
            );
        } else {
            actionDeleteAccount(accountId);
        }
    }

    public void onStartChangeRewardAddress() {
        if (!account.hasPrivateKey) {
            showDialog(Dialog_WatchMode.newInstance());
            return;
        }

        Disposable disposable = accountsInteractor
                .selectAccount(account.id)
                .subscribeOn(AppSchedulers.INSTANCE.io())
                .observeOn(AppSchedulers.INSTANCE.ui())
                .doOnError(error -> WLog.e(error.toString()))
                .subscribe(
                        () -> {
                            Intent changeAddress = new Intent(AccountDetailActivity.this, RewardAddressChangeActivity.class);
                            changeAddress.putExtra("currentAddresses", mRewardAddress.getText().toString());
                            startActivity(changeAddress);
                        },
                        error -> {
                            Toast.makeText(getBaseContext(), R.string.str_unknown_error_msg, Toast.LENGTH_SHORT).show();
                        }
                );
        compositeDisposable.add(disposable);
    }

    private void onInitView() {
        if (getIntent() == null || getIntent().getLongExtra("id", -1L) <= 0L) {
            onBackPressed();
        }

        account = accountsInteractor.getAccount(getIntent().getLongExtra("id", -1L)).blockingGet();

        if (account == null) onBackPressed();

        baseChain = BaseChain.getChain(account.baseChain);
        if (baseChain == null) onBackPressed();

        WDp.showChainDp(AccountDetailActivity.this, baseChain, mCardName, mCardBody, mCardRewardAddress);
        mChainImg.setImageResource(baseChain.getChainIcon());

        if (baseChain.isGRPC()) {
            new WithdrawAddressGrpcTask(getBaseApplication(), this, baseChain, account).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new NodeInfoGrpcTask(getBaseApplication(), this, baseChain).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new NodeInfoTask(getBaseApplication(), this, baseChain).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        mAccountName.setText(account.getAccountTitle(this));
        mAccountAddress.setText(account.address);
        mAccountGenTime.setText(WDp.getDpTime(getBaseContext(), account.importTime));

        if (account.hasPrivateKey && account.fromMnemonic) {
            mAccountState.setText(getString(R.string.str_with_mnemonic));
            mAccountPath.setText(baseChain.getPathProvider().getPathString(account.path, account.customPath));
            mPathLayer.setVisibility(View.VISIBLE);
            mImportMsg.setVisibility(View.GONE);
            mBtnCheck.setVisibility(View.VISIBLE);
            mBtnCheckKey.setVisibility(View.VISIBLE);
            mBtnCheck.setText(getString(R.string.str_check_mnemonic));
            mBtnCheckKey.setText(getString(R.string.str_check_private_key));

        } else if (account.hasPrivateKey) {
            mAccountState.setText(getString(R.string.str_with_privatekey));
            mPathLayer.setVisibility(View.GONE);
            mImportMsg.setVisibility(View.GONE);
            mBtnCheck.setVisibility(View.GONE);
            mView.setVisibility(View.GONE);
            mBtnCheckKey.setVisibility(View.VISIBLE);
            mBtnCheckKey.setText(getString(R.string.str_check_private_key));
            if (baseChain.equals(OKEX_MAIN.INSTANCE)) {
                mPathLayer.setVisibility(View.VISIBLE);
                mAccountPathTitle.setText(R.string.str_address_type);
                if (account.customPath > 0) {
                    mAccountPath.setText(R.string.str_ethereum_type_address);
                } else {
                    mAccountPath.setText(R.string.str_legacy_tendermint_type_address);
                }
                mAccountPath.setTextColor(ContextCompat.getColor(this, R.color.colorPhoton));
            }

        } else {
            mAccountState.setText(getString(R.string.str_only_address));
            mPathLayer.setVisibility(View.GONE);
            mImportMsg.setVisibility(View.VISIBLE);
            mImportMsg.setTextColor(WDp.getChainColor(getBaseContext(), baseChain));
            mBtnCheck.setVisibility(View.VISIBLE);
            mBtnCheckKey.setVisibility(View.VISIBLE);
            mBtnCheck.setText(getString(R.string.str_import_mnemonic));
            mBtnCheckKey.setText(getString(R.string.str_import_key));
        }

    }

    public void onChangeNickName(String name) {
        account.nickName = name;

        Disposable disposable = accountsInteractor
                .updateAccountNickName(
                        account.id,
                        name
                )
                .subscribeOn(AppSchedulers.INSTANCE.io())
                .observeOn(AppSchedulers.INSTANCE.ui())
                .doOnError(error -> WLog.e(error.toString()))
                .subscribe(
                        this::onInitView
                );
        compositeDisposable.add(disposable);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mBtnCheck)) {
            if (account.hasPrivateKey) {
                final Intent intent = new Intent(this, CheckPasswordActivity.class);
                launcherCheckMnemonic.launch(
                        intent,
                        ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_bottom, R.anim.fade_out)
                );
            } else {
                Intent restoreIntent = new Intent(AccountDetailActivity.this, MnemonicRestoreActivity.class);
                restoreIntent.putExtra("chain", baseChain.getChainName());
                startActivity(restoreIntent);
            }

        } else if (v.equals(mBtnCheckKey)) {
            if (account.hasPrivateKey) {
                final Intent intent = new Intent(this, CheckPasswordActivity.class);
                launcherCheckProvateKey.launch(
                        intent,
                        ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_bottom, R.anim.fade_out)
                );
            } else {
                Intent restoreIntent = new Intent(AccountDetailActivity.this, PrivateKeyRestoreActivity.class);
                restoreIntent.putExtra("chain", baseChain.getChainName());
                startActivity(restoreIntent);
            }

        } else if (v.equals(mBtnDelete)) {
            int accountSum = 0;
            for (BaseChain baseChain : getBaseDao().dpSortedChains()) {
                accountSum = accountSum + accountsInteractor.getChainAccounts(baseChain).size();
            }
            if (accountSum <= 1) {
                Toast.makeText(AccountDetailActivity.this, getString(R.string.error_reserve_1_account), Toast.LENGTH_SHORT).show();
                return;
            }

            showDialog(DeleteConfirmDialogFragment.Companion.newInstance(account.id));

        } else if (v.equals(mNameEditImg)) {
            Bundle bundle = new Bundle();
            bundle.putLong("id", account.id);
            bundle.putString("name", account.nickName);
            showDialog(Dialog_ChangeNickName.newInstance(bundle));

        } else if (v.equals(mBtnQr)) {
            AccountShowDialogFragment show = AccountShowDialogFragment.Companion.newInstance(
                    mAccountName.getText().toString(),
                    account.address
            );
            showDialog(show);
        } else if (v.equals(mBtnRewardAddressChange)) {
            if (!account.hasPrivateKey) {
                Dialog_WatchMode add = Dialog_WatchMode.newInstance();
                showDialog(add);
                return;
            }

            if (TextUtils.isEmpty(mRewardAddress.getText().toString())) {
                Toast.makeText(getBaseContext(), R.string.error_network_error, Toast.LENGTH_SHORT).show();

                return;
            }

            Dialog_RewardAddressChangeInfo change = Dialog_RewardAddressChangeInfo.newInstance();
            showDialog(change);
        }

    }


    @Override
    public void onTaskResponse(TaskResult result) {
        if (result.taskType == TASK_GRPC_FETCH_WITHDRAW_ADDRESS) {
            String rewardAddress = (String) result.resultData;
            if (!TextUtils.isEmpty(rewardAddress)) {
                mRewardAddress.setText(rewardAddress.trim());
                if (rewardAddress.equals(account.address)) {
                    mRewardAddress.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                } else {
                    mRewardAddress.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
                }
            }

        } else if (result.taskType == TASK_FETCH_NODE_INFO) {
            NodeInfo nodeinfo = (NodeInfo) result.resultData;
            if (nodeinfo != null) {
                mAccountChain.setText(nodeinfo.network);
            }

        } else if (result.taskType == TASK_GRPC_FETCH_NODE_INFO) {
            tendermint.p2p.Types.NodeInfo nodeinfo = (tendermint.p2p.Types.NodeInfo) result.resultData;
            if (nodeinfo != null) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> mAccountChain.setText(nodeinfo.getNetwork()), 100);

            }
        }
    }


    private void actionDeleteAccount(Long accountId) {
        showWaitDialog();
        Disposable disposable =
                Completable.mergeArray(
                        accountsInteractor.deleteAccount(accountId),
                        balancesInteractor.deleteBalances(accountId)
                )
                        .subscribeOn(AppSchedulers.INSTANCE.io())
                        .observeOn(AppSchedulers.INSTANCE.ui())
                        .doOnError(error -> WLog.e(error.toString()))
                        .subscribe(
                                () -> {
                                    WLog.w("Account was selected after removing");
                                    startMainActivity(0);
                                },
                                error -> {
                                    Toast.makeText(getBaseContext(), R.string.str_unknown_error_msg, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, IntroActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                        );
        compositeDisposable.add(disposable);
    }

    private void actionCheckMnemonic(Long accountId) {
        Intent checkintent = new Intent(AccountDetailActivity.this, ShowMnemonicActivity.class);
        checkintent.putExtra(ShowMnemonicActivity.KEY_ACCOUNT_ID, accountId);
        startActivity(checkintent);
    }

    private void actionCheckPrivateKey(Long accountId) {
        Intent checkintent = new Intent(AccountDetailActivity.this, ShowPrivateKeyActivity.class);
        checkintent.putExtra(ShowMnemonicActivity.KEY_ACCOUNT_ID, accountId);
        startActivity(checkintent);
    }
}
