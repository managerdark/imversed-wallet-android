package wannabit.io.cosmostaion.utils;

import static com.fulldive.wallet.models.BaseChain.AKASH_MAIN;
import static com.fulldive.wallet.models.BaseChain.ALTHEA_MAIN;
import static com.fulldive.wallet.models.BaseChain.ALTHEA_TEST;
import static com.fulldive.wallet.models.BaseChain.AXELAR_MAIN;
import static com.fulldive.wallet.models.BaseChain.BAND_MAIN;
import static com.fulldive.wallet.models.BaseChain.BITCANNA_MAIN;
import static com.fulldive.wallet.models.BaseChain.BITSONG_MAIN;
import static com.fulldive.wallet.models.BaseChain.BNB_MAIN;
import static com.fulldive.wallet.models.BaseChain.CERBERUS_MAIN;
import static com.fulldive.wallet.models.BaseChain.CERTIK_MAIN;
import static com.fulldive.wallet.models.BaseChain.CHIHUAHUA_MAIN;
import static com.fulldive.wallet.models.BaseChain.COMDEX_MAIN;
import static com.fulldive.wallet.models.BaseChain.COSMOS_MAIN;
import static com.fulldive.wallet.models.BaseChain.COSMOS_TEST;
import static com.fulldive.wallet.models.BaseChain.CRYPTO_MAIN;
import static com.fulldive.wallet.models.BaseChain.CUDOS_MAIN;
import static com.fulldive.wallet.models.BaseChain.DESMOS_MAIN;
import static com.fulldive.wallet.models.BaseChain.EMONEY_MAIN;
import static com.fulldive.wallet.models.BaseChain.EVMOS_MAIN;
import static com.fulldive.wallet.models.BaseChain.FETCHAI_MAIN;
import static com.fulldive.wallet.models.BaseChain.GRABRIDGE_MAIN;
import static com.fulldive.wallet.models.BaseChain.IMVERSED_MAIN;
import static com.fulldive.wallet.models.BaseChain.INJ_MAIN;
import static com.fulldive.wallet.models.BaseChain.IOV_MAIN;
import static com.fulldive.wallet.models.BaseChain.IRIS_MAIN;
import static com.fulldive.wallet.models.BaseChain.IRIS_TEST;
import static com.fulldive.wallet.models.BaseChain.JUNO_MAIN;
import static com.fulldive.wallet.models.BaseChain.KAVA_MAIN;
import static com.fulldive.wallet.models.BaseChain.KI_MAIN;
import static com.fulldive.wallet.models.BaseChain.KONSTELL_MAIN;
import static com.fulldive.wallet.models.BaseChain.LUM_MAIN;
import static com.fulldive.wallet.models.BaseChain.MEDI_MAIN;
import static com.fulldive.wallet.models.BaseChain.OKEX_MAIN;
import static com.fulldive.wallet.models.BaseChain.OMNIFLIX_MAIN;
import static com.fulldive.wallet.models.BaseChain.OSMOSIS_MAIN;
import static com.fulldive.wallet.models.BaseChain.PERSIS_MAIN;
import static com.fulldive.wallet.models.BaseChain.PROVENANCE_MAIN;
import static com.fulldive.wallet.models.BaseChain.REGEN_MAIN;
import static com.fulldive.wallet.models.BaseChain.RIZON_MAIN;
import static com.fulldive.wallet.models.BaseChain.SECRET_MAIN;
import static com.fulldive.wallet.models.BaseChain.SENTINEL_MAIN;
import static com.fulldive.wallet.models.BaseChain.SIF_MAIN;
import static com.fulldive.wallet.models.BaseChain.STARGAZE_MAIN;
import static com.fulldive.wallet.models.BaseChain.UMEE_MAIN;
import static wannabit.io.cosmostaion.base.BaseConstant.*;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fulldive.wallet.models.BaseChain;
import com.fulldive.wallet.models.Currency;
import com.fulldive.wallet.models.WalletBalance;
import com.fulldive.wallet.presentation.main.MainActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import cosmos.base.v1beta1.CoinOuterClass;
import cosmos.distribution.v1beta1.Distribution;
import cosmos.staking.v1beta1.Staking;
import kava.cdp.v1beta1.Genesis;
import kava.hard.v1beta1.Hard;
import osmosis.gamm.poolmodels.balancer.BalancerPool;
import osmosis.incentives.GaugeOuterClass;
import osmosis.lockup.Lock;
import osmosis.poolincentives.v1beta1.QueryOuterClass;
import sifnode.clp.v1.Querier;
import starnamed.x.starname.v1beta1.Types;
import tendermint.liquidity.v1beta1.Liquidity;
import wannabit.io.cosmostaion.R;
import wannabit.io.cosmostaion.base.BaseActivity;
import wannabit.io.cosmostaion.base.BaseConstant;
import wannabit.io.cosmostaion.base.BaseData;
import wannabit.io.cosmostaion.dao.Account;
import wannabit.io.cosmostaion.dao.Assets;
import wannabit.io.cosmostaion.dao.BnbTicker;
import wannabit.io.cosmostaion.dao.ChainParam;
import wannabit.io.cosmostaion.dao.Cw20Assets;
import wannabit.io.cosmostaion.dao.IbcToken;
import wannabit.io.cosmostaion.model.GDexManager;
import wannabit.io.cosmostaion.model.UnbondingInfo;
import wannabit.io.cosmostaion.model.type.Coin;
import wannabit.io.cosmostaion.model.type.Validator;
import wannabit.io.cosmostaion.network.res.ResBnbAccountInfo;
import wannabit.io.cosmostaion.network.res.ResLcdKavaAccountInfo;
import wannabit.io.cosmostaion.network.res.ResOkAccountInfo;

public class WUtil {

    public static Account getAccountFromBnbLcd(long id, ResBnbAccountInfo lcd) {
        Account result = new Account();
        result.id = id;
        result.address = lcd.address;
        result.sequenceNumber = Integer.parseInt(lcd.sequence);
        result.accountNumber = Integer.parseInt(lcd.account_number);
        return result;
    }

    public static Account getAccountFromKavaLcd(long id, ResLcdKavaAccountInfo lcd) {
        Account result = new Account();
        result.id = id;
        if (lcd.result != null && lcd.height != null) {
            if (lcd.result.type.equals(COSMOS_AUTH_TYPE_ACCOUNT)) {
                result.address = lcd.result.value.address;
                result.sequenceNumber = Integer.parseInt(lcd.result.value.sequence);
                result.accountNumber = Integer.parseInt(lcd.result.value.account_number);

            } else if (lcd.result.type.equals(BaseConstant.COSMOS_AUTH_TYPE_VESTING_ACCOUNT) || lcd.result.type.equals(COSMOS_AUTH_TYPE_P_VESTING_ACCOUNT)) {
                result.address = lcd.result.value.address;
                result.sequenceNumber = Integer.parseInt(lcd.result.value.sequence);
                result.accountNumber = Integer.parseInt(lcd.result.value.account_number);
            }
        }
        return result;
    }

    public static Account getAccountFromOkLcd(long id, ResOkAccountInfo lcd) {
        Account result = new Account();
        result.id = id;
        if (lcd.type.equals(COSMOS_AUTH_TYPE_OKEX_ACCOUNT)) {
            result.address = lcd.value.eth_address;
            result.sequenceNumber = Integer.parseInt(lcd.value.sequence);
            result.accountNumber = Integer.parseInt(lcd.value.account_number);
        }
        return result;
    }

    public static List<WalletBalance> getBalancesFromBnbLcd(long accountId, ResBnbAccountInfo lcd) {
        final long time = System.currentTimeMillis();
        ArrayList<WalletBalance> result = new ArrayList<>();
        if (lcd.balances != null && lcd.balances.size() > 0) {
            for (ResBnbAccountInfo.BnbBalance coin : lcd.balances) {
                WalletBalance temp = new WalletBalance(
                        0L,
                        accountId,
                        coin.symbol,
                        coin.free,
                        coin.locked,
                        coin.frozen,
                        time
                );
                result.add(temp);
            }
        }
        return result;
    }

    public static List<WalletBalance> getBalancesFromKavaLcd(long accountId, ResLcdKavaAccountInfo lcd) {
        long time = System.currentTimeMillis();
        ArrayList<WalletBalance> result = new ArrayList<>();
        if (lcd != null && lcd.result != null && lcd.height != null) {
            if (lcd.result.type.equals(COSMOS_AUTH_TYPE_ACCOUNT)) {
                if (lcd.result.value.coins != null && lcd.result.value.coins.size() > 0) {
                    for (Coin coin : lcd.result.value.coins) {
                        WalletBalance temp = new WalletBalance(
                                0L,
                                accountId,
                                coin.denom,
                                coin.amount,
                                "",
                                "",
                                time
                        );
                        result.add(temp);
                    }
                }
                return result;

            } else if (lcd.result.type.equals(BaseConstant.COSMOS_AUTH_TYPE_VESTING_ACCOUNT) || lcd.result.type.equals(COSMOS_AUTH_TYPE_P_VESTING_ACCOUNT)) {
                BigDecimal dpBalance = BigDecimal.ZERO;
                BigDecimal dpVesting = BigDecimal.ZERO;
                BigDecimal originalVesting = BigDecimal.ZERO;
                BigDecimal remainVesting = BigDecimal.ZERO;
                BigDecimal delegatedVesting = BigDecimal.ZERO;

                if (lcd.result.value.coins != null && lcd.result.value.coins.size() > 0) {
                    for (Coin coin : lcd.result.value.coins) {
                        if (coin.denom.equals(TOKEN_KAVA)) {
                            dpBalance = BigDecimal.ZERO;
                            dpVesting = BigDecimal.ZERO;
                            originalVesting = BigDecimal.ZERO;
                            remainVesting = BigDecimal.ZERO;
                            delegatedVesting = BigDecimal.ZERO;
                            dpBalance = new BigDecimal(coin.amount);

                            if (lcd.result.value.original_vesting != null && lcd.result.value.original_vesting.size() > 0) {
                                for (Coin vesting : lcd.result.value.original_vesting) {
                                    if (vesting.denom.equals(TOKEN_KAVA)) {
                                        originalVesting = originalVesting.add(new BigDecimal(vesting.amount));
                                    }
                                }
                            }

                            if (lcd.result.value.delegated_vesting != null && lcd.result.value.delegated_vesting.size() > 0) {
                                for (Coin vesting : lcd.result.value.delegated_vesting) {
                                    if (vesting.denom.equals(TOKEN_KAVA)) {
                                        delegatedVesting = delegatedVesting.add(new BigDecimal(vesting.amount));
                                    }
                                }
                            }

                            WLog.w("kava dpBalance " + dpBalance);
                            WLog.w("kava originalVesting " + originalVesting);
                            WLog.w("kava delegatedVesting " + delegatedVesting);

                            remainVesting = lcd.result.value.getCalcurateVestingAmountSumByDenom(TOKEN_KAVA);
                            WLog.w("kava remainVesting " + remainVesting);

                            dpVesting = remainVesting.subtract(delegatedVesting);
                            WLog.w("kava  dpVesting " + dpVesting);
                            if (dpVesting.compareTo(BigDecimal.ZERO) <= 0) {
                                dpVesting = BigDecimal.ZERO;
                            }
                            WLog.w("kava  dpVesting1 " + dpVesting);

                            if (remainVesting.compareTo(delegatedVesting) > 0) {
                                dpBalance = dpBalance.subtract(remainVesting).add(delegatedVesting);
                            }
                            WLog.w("kava dpBalancee " + dpBalance);

                            WalletBalance temp = new WalletBalance(
                                    0L,
                                    accountId,
                                    TOKEN_KAVA,
                                    dpBalance.toPlainString(),
                                    delegatedVesting.toPlainString(),
                                    dpVesting.toPlainString(),
                                    time
                            );
                            result.add(temp);
                        } else if (coin.denom.equals(TOKEN_HARD)) {
                            dpBalance = BigDecimal.ZERO;
                            dpVesting = BigDecimal.ZERO;
                            originalVesting = BigDecimal.ZERO;
                            remainVesting = BigDecimal.ZERO;
                            delegatedVesting = BigDecimal.ZERO;
                            dpBalance = new BigDecimal(coin.amount);

                            if (lcd.result.value.original_vesting != null && lcd.result.value.original_vesting.size() > 0) {
                                for (Coin vesting : lcd.result.value.original_vesting) {
                                    if (vesting.denom.equals(TOKEN_HARD)) {
                                        originalVesting = originalVesting.add(new BigDecimal(vesting.amount));
                                    }
                                }
                            }
                            WLog.w("hard dpBalance " + dpBalance);
                            WLog.w("hard originalVesting " + originalVesting);
                            remainVesting = lcd.result.value.getCalcurateVestingAmountSumByDenom(TOKEN_HARD);

                            dpBalance = dpBalance.subtract(remainVesting);
                            WLog.w("hard dpBalancee " + dpBalance);

                            WalletBalance temp = new WalletBalance(
                                    0L,
                                    accountId,
                                    coin.denom,
                                    dpBalance.toPlainString(),
                                    remainVesting.toPlainString(),
                                    "",
                                    time
                            );
                            result.add(temp);

                        } else if (coin.denom.equals(TOKEN_SWP)) {
                            dpBalance = BigDecimal.ZERO;
                            dpVesting = BigDecimal.ZERO;
                            originalVesting = BigDecimal.ZERO;
                            remainVesting = BigDecimal.ZERO;
                            delegatedVesting = BigDecimal.ZERO;
                            dpBalance = new BigDecimal(coin.amount);

                            if (lcd.result.value.original_vesting != null && lcd.result.value.original_vesting.size() > 0) {
                                for (Coin vesting : lcd.result.value.original_vesting) {
                                    if (vesting.denom.equals(TOKEN_SWP)) {
                                        originalVesting = originalVesting.add(new BigDecimal(vesting.amount));
                                    }
                                }
                            }
                            WLog.w("TOKEN_SWP dpBalance " + dpBalance);
                            WLog.w("TOKEN_SWP originalVesting " + originalVesting);
                            remainVesting = lcd.result.value.getCalcurateVestingAmountSumByDenom(TOKEN_SWP);

                            dpBalance = dpBalance.subtract(remainVesting);
                            WLog.w("TOKEN_SWP dpBalancee " + dpBalance);

                            WalletBalance temp = new WalletBalance(
                                    0L,
                                    accountId,
                                    coin.denom,
                                    dpBalance.toPlainString(),
                                    remainVesting.toPlainString(),
                                    "",
                                    time
                            );

                            result.add(temp);

                        } else {
                            WalletBalance temp = new WalletBalance(
                                    0L,
                                    accountId,
                                    coin.denom,
                                    coin.amount,
                                    "",
                                    "",
                                    time
                            );
                            result.add(temp);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static String prettyPrinter(Object object) {
        String result = "";
        try {
            result = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Exception e) {
            result = "Print json error";
        }
        return result;
    }

    public static byte[] integerToBytes(BigInteger s, int length) {
        byte[] bytes = s.toByteArray();

        if (length < bytes.length) {
            byte[] tmp = new byte[length];
            System.arraycopy(bytes, bytes.length - tmp.length, tmp, 0, tmp.length);
            return tmp;
        } else if (length > bytes.length) {
            byte[] tmp = new byte[length];
            System.arraycopy(bytes, 0, tmp, tmp.length - bytes.length, bytes.length);
            return tmp;
        }
        return bytes;
    }

    /**
     * Sorts
     */
    public static List<Validator> onSortByValidatorName(List<Validator> validators) {
        List<Validator> result = new ArrayList<>(validators);
        Collections.sort(result, (o1, o2) -> {
            return o1.description.moniker.compareTo(o2.description.moniker);
        });
        Collections.sort(result, (o1, o2) -> {
            if (o1.jailed && !o2.jailed) return 1;
            else if (!o1.jailed && o2.jailed) return -1;
            else return 0;
        });
        return result;
    }

    public static List<Staking.Validator> onSortByValidatorNameV1(List<Staking.Validator> validators) {
        List<Staking.Validator> result = new ArrayList<>(validators);
        Collections.sort(result, (o1, o2) -> {
            return o1.getDescription().getMoniker().compareTo(o2.getDescription().getMoniker());
        });
        Collections.sort(result, (o1, o2) -> {
            if (o1.getJailed() && !o2.getJailed()) return 1;
            else if (!o1.getJailed() && o2.getJailed()) return -1;
            else return 0;
        });
        return result;
    }

    public static List<Validator> onSortByValidatorPower(List<Validator> validators) {
        List<Validator> result = new ArrayList<>(validators);
        Collections.sort(result, (o1, o2) -> {
            return Double.compare(Double.parseDouble(o2.tokens), Double.parseDouble(o1.tokens));
        });
        Collections.sort(result, (o1, o2) -> {
            if (o1.jailed && !o2.jailed) return 1;
            else if (!o1.jailed && o2.jailed) return -1;
            else return 0;
        });
        return result;
    }

    public static List<Staking.Validator> onSortByValidatorPowerV1(List<Staking.Validator> validators) {
        List<Staking.Validator> result = new ArrayList<>(validators);
        Collections.sort(result, (o1, o2) -> {
            return Double.compare(Double.parseDouble(o2.getTokens()), Double.parseDouble(o1.getTokens()));
        });
        Collections.sort(result, (o1, o2) -> {
            if (o1.getJailed() && !o2.getJailed()) return 1;
            else if (!o1.getJailed() && o2.getJailed()) return -1;
            else return 0;
        });
        return result;
    }

    public static List<Validator> onSortByOKValidatorPower(List<Validator> validators) {
        List<Validator> result = new ArrayList<>(validators);
        Collections.sort(result, (o1, o2) -> Double.compare(Double.parseDouble(o2.delegator_shares), Double.parseDouble(o1.delegator_shares)));
        Collections.sort(result, (o1, o2) -> {
            if (o1.jailed && !o2.jailed) return 1;
            else if (!o1.jailed && o2.jailed) return -1;
            else return 0;
        });
        return result;
    }


    public static List<Validator> onSortByDelegate(List<Validator> validators, final BaseData dao) {
        List<Validator> result = new ArrayList<>(validators);
        Collections.sort(result, (o1, o2) -> {
            BigDecimal bondingO1 = dao.delegatedAmountByValidator(o1.operator_address);
            BigDecimal bondingO2 = dao.delegatedAmountByValidator(o2.operator_address);
            return bondingO2.compareTo(bondingO1);

        });
        Collections.sort(result, (o1, o2) -> {
            if (o1.jailed && !o2.jailed) return 1;
            else if (!o1.jailed && o2.jailed) return -1;
            else return 0;
        });
        return result;
    }

    public static List<Staking.Validator> onSortByDelegateV1(List<Staking.Validator> validators, final BaseData dao) {
        List<Staking.Validator> result = new ArrayList<>(validators);
        Collections.sort(result, (o1, o2) -> {
            BigDecimal bondingO1 = dao.getDelegation(o1.getOperatorAddress());
            BigDecimal bondingO2 = dao.getDelegation(o2.getOperatorAddress());
            return bondingO2.compareTo(bondingO1);
        });
        Collections.sort(result, (o1, o2) -> {
            if (o1.getJailed() && !o2.getJailed()) return 1;
            else if (!o1.getJailed() && o2.getJailed()) return -1;
            else return 0;
        });
        return result;
    }

    public static List<Validator> onSortByReward(List<Validator> validators, String denom, BaseData basedata) {
        List<Validator> result = new ArrayList<>(validators);
        Collections.sort(result, (o1, o2) -> {
            BigDecimal rewardO1 = basedata.rewardAmountByValidator(denom, o1.operator_address);
            BigDecimal rewardO2 = basedata.rewardAmountByValidator(denom, o2.operator_address);
            return rewardO2.compareTo(rewardO1);
        });
        Collections.sort(result, (o1, o2) -> {
            if (o1.jailed && !o2.jailed) return 1;
            else if (!o1.jailed && o2.jailed) return -1;
            else return 0;
        });
        return result;
    }

    public static List<Staking.Validator> onSortByRewardV1(List<Staking.Validator> validators, String denom, final BaseData dao) {
        List<Staking.Validator> result = new ArrayList<>(validators);
        Collections.sort(result, (o1, o2) -> {
            BigDecimal rewardO1 = dao.getReward(denom, o1.getOperatorAddress());
            BigDecimal rewardO2 = dao.getReward(denom, o2.getOperatorAddress());
            return rewardO2.compareTo(rewardO1);
        });
        Collections.sort(result, (o1, o2) -> {
            if (o1.getJailed() && !o2.getJailed()) return 1;
            else if (!o1.getJailed() && o2.getJailed()) return -1;
            else return 0;
        });
        return result;
    }

    public static List<Validator> onSortByOnlyReward(List<Validator> validators, String denom, BaseData basedata) {
        List<Validator> result = new ArrayList<>(validators);
        Collections.sort(result, (o1, o2) -> {
            BigDecimal rewardO1 = basedata.rewardAmountByValidator(denom, o1.operator_address);
            BigDecimal rewardO2 = basedata.rewardAmountByValidator(denom, o2.operator_address);
            return rewardO2.compareTo(rewardO1);
        });
        return result;
    }

    public static void onSortRewardAmount(ArrayList<Distribution.DelegationDelegatorReward> rewards, String denom) {
        Collections.sort(rewards, (o1, o2) -> {
            BigDecimal rewardO1 = getGrpcRewardAmount(o1, denom);
            BigDecimal rewardO2 = getGrpcRewardAmount(o2, denom);
            return rewardO2.compareTo(rewardO1);
        });
    }

    public static BigDecimal getGrpcRewardAmount(Distribution.DelegationDelegatorReward reward, String denom) {
        return decCoinAmount(reward.getRewardList(), denom);
    }

    public static BigDecimal decCoinAmount(List<CoinOuterClass.DecCoin> coins, String denom) {
        for (CoinOuterClass.DecCoin coin : coins) {
            if (coin.getDenom().equals(denom)) {
                return new BigDecimal(coin.getAmount()).movePointLeft(18).setScale(0, RoundingMode.DOWN);
            }
        }
        return BigDecimal.ZERO;
    }

    public static List<Validator> onSortingByCommission(List<Validator> validators) {
        List<Validator> result = new ArrayList<>(validators);
        Collections.sort(result, (o1, o2) -> {
            if (Float.parseFloat(o1.commission.commission_rates.rate) > Float.parseFloat(o2.commission.commission_rates.rate))
                return 1;
            else if (Float.parseFloat(o1.commission.commission_rates.rate) < Float.parseFloat(o2.commission.commission_rates.rate))
                return -1;
            else return 0;
        });
        Collections.sort(result, (o1, o2) -> {
            if (o1.jailed && !o2.jailed) return 1;
            else if (!o1.jailed && o2.jailed) return -1;
            else return 0;
        });
        return result;
    }

    public static List<Staking.Validator> onSortingByCommissionV1(List<Staking.Validator> validators) {
        List<Staking.Validator> result = new ArrayList<>(validators);
        Collections.sort(result, (o1, o2) -> {
            return Float.compare(
                    Float.parseFloat(o1.getCommission().getCommissionRates().getRate()),
                    Float.parseFloat(o2.getCommission().getCommissionRates().getRate())
            );
        });
        Collections.sort(result, (o1, o2) -> {
            if (o1.getJailed() && !o2.getJailed()) return 1;
            else if (!o1.getJailed() && o2.getJailed()) return -1;
            else return 0;
        });
        return result;
    }

    public static void onSortingDenom(List<String> denom, BaseChain chain) {
        Collections.sort(denom, (o1, o2) -> {
            if (o1.equals(chain.getMainDenom())) return -1;
            if (o2.equals(chain.getMainDenom())) return 1;

            if (chain.equals(KAVA_MAIN.INSTANCE)) {
                if (o1.equals(TOKEN_HARD)) return -1;
                if (o2.equals(TOKEN_HARD)) return 1;

            }
            return 0;
        });
    }

    public static void onSortingNativeCoins(List<WalletBalance> balances, final BaseChain chain) {
        Collections.sort(balances, (o1, o2) -> {
            if (o1.getDenom().equals(chain.getMainDenom())) return -1;
            if (o2.getDenom().equals(chain.getMainDenom())) return 1;

            if (chain.equals(KAVA_MAIN.INSTANCE)) {
                if (o1.getDenom().equals(TOKEN_HARD)) return -1;
                if (o2.getDenom().equals(TOKEN_HARD)) return 1;

            } else if (chain.equals(OKEX_MAIN.INSTANCE)) {
                if (o1.getDenom().equals("okb-c4d")) return -1;
                if (o2.getDenom().equals("okb-c4d")) return 1;
            }
            return o1.getDenom().compareTo(o2.getDenom());
        });
    }

    public static void onSortingCoins(List<Coin> coins, BaseChain chain) {
        Collections.sort(coins, (o1, o2) -> {
            if (o1.denom.equals(chain.getMainDenom())) return -1;
            if (o2.denom.equals(chain.getMainDenom())) return 1;
            else return 0;
        });
    }

    public static void onSortingBalance(List<WalletBalance> coins, BaseChain chain) {
        Collections.sort(coins, (o1, o2) -> {
            if (o1.getDenom().equals(chain.getMainDenom())) return -1;
            if (o2.getDenom().equals(chain.getMainDenom())) return 1;
            else return 0;
        });
    }

    public static void onSortingOsmosisPool(List<WalletBalance> coins) {
        Collections.sort(coins, (o1, o2) -> {
            if (o1.osmosisAmmPoolId() < o2.osmosisAmmPoolId()) return -1;
            else if (o1.osmosisAmmPoolId() > o2.osmosisAmmPoolId()) return 1;
            return 0;
        });
    }

    public static void onSortingGravityPool(List<WalletBalance> coins, BaseData baseData) {
        Collections.sort(coins, (o1, o2) -> {
            long id1 = baseData.getGravityPoolByDenom(o1.getDenom()).getId();
            long id2 = baseData.getGravityPoolByDenom(o2.getDenom()).getId();
            return Long.compare(id1, id2);
        });
    }

    public static void onSortingInjectivePool(List<WalletBalance> coins) {
        Collections.sort(coins, (o1, o2) -> {
            if (o1.injectivePoolId() < o2.injectivePoolId()) return -1;
            else if (o1.injectivePoolId() > o2.injectivePoolId()) return 1;
            return 0;
        });
    }


    public static List<UnbondingInfo.DpEntry> onSortUnbondingsRecent(Context c, List<UnbondingInfo> unbondingInfos) {
        ArrayList<UnbondingInfo.DpEntry> result = new ArrayList<>();
        for (UnbondingInfo unbondingInfo : unbondingInfos) {
            for (UnbondingInfo.Entry entry : unbondingInfo.entries) {
                result.add(new UnbondingInfo.DpEntry(unbondingInfo.validator_address, entry.completion_time, entry.balance));
            }
        }

        Collections.sort(result, (o1, o2) -> Long.compare(
                WDp.dateToLong(c, o1.completion_time),
                WDp.dateToLong(c, o2.completion_time)
        ));
        return result;
    }

    public static List<UnbondingInfo.DpEntry> onSortUnbondingsRecent_Grpc(List<Staking.UnbondingDelegation> unbondingGrpcInfos) {
        ArrayList<UnbondingInfo.DpEntry> result = new ArrayList<>();
        for (Staking.UnbondingDelegation unbondingGrpcInfo : unbondingGrpcInfos) {
            for (Staking.UnbondingDelegationEntry entry : unbondingGrpcInfo.getEntriesList()) {
                result.add(new UnbondingInfo.DpEntry(unbondingGrpcInfo.getValidatorAddress(), String.valueOf(entry.getCompletionTime().getSeconds()), entry.getBalance()));
            }
        }

        Collections.sort(result, (o1, o2) -> Long.compare(
                Long.parseLong(o1.completion_time),
                Long.parseLong(o2.completion_time)
        ));
        return result;
    }

    /**
     * @memo size
     */
    public static int getMaxMemoSize(BaseChain chain) {
        if (chain.equals(BNB_MAIN.INSTANCE)) {
            return BaseConstant.MEMO_BNB;
        }
        return BaseConstant.MEMO_ATOM;
    }

    public static int getCharSize(String memo) {
        int result = 1000;
        try {
            result = memo.trim().getBytes(StandardCharsets.UTF_8).length;
        } catch (Exception e) {
        }

        return result;
    }

    /**
     * coin decimal
     */
    public static int getKavaCoinDecimal(Coin coin) {
        if (coin.denom.equalsIgnoreCase(TOKEN_KAVA)) {
            return 6;
        } else if (coin.denom.equalsIgnoreCase(TOKEN_HARD)) {
            return 6;
        } else if (coin.denom.equalsIgnoreCase("xrpb") || coin.denom.equalsIgnoreCase("xrbp")) {
            return 8;
        } else if (coin.denom.equalsIgnoreCase("btc")) {
            return 8;
        } else if (coin.denom.equalsIgnoreCase("usdx")) {
            return 6;
        } else if (coin.denom.equalsIgnoreCase("bnb")) {
            return 8;
        } else if (coin.denom.equalsIgnoreCase("btcb") || coin.denom.equalsIgnoreCase("hbtc")) {
            return 8;
        } else if (coin.denom.equalsIgnoreCase("busd")) {
            return 8;
        } else if (coin.denom.equalsIgnoreCase("swp")) {
            return 6;
        }
        return 0;

    }

    public static int getKavaCoinDecimal(BaseData baseData, String denom) {
        if (denom.equalsIgnoreCase(TOKEN_KAVA)) {
            return 6;
        } else if (denom.equalsIgnoreCase(TOKEN_HARD)) {
            return 6;
        } else if (denom.equalsIgnoreCase("xrpb") || denom.equalsIgnoreCase("xrbp")) {
            return 8;
        } else if (denom.equalsIgnoreCase("btc")) {
            return 8;
        } else if (denom.equalsIgnoreCase("usdx")) {
            return 6;
        } else if (denom.equalsIgnoreCase("bnb")) {
            return 8;
        } else if (denom.equalsIgnoreCase("btcb") || denom.equalsIgnoreCase("hbtc")) {
            return 8;
        } else if (denom.equalsIgnoreCase("busd")) {
            return 8;
        } else if (denom.equalsIgnoreCase("swp")) {
            return 6;
        } else if (denom.startsWith("ibc/")) {
            return getIbcDecimal(baseData, denom);
        }
        return 6;
    }

    public static int getSifCoinDecimal(BaseData baseData, String denom) {
        if (denom != null) {
            if (denom.equalsIgnoreCase(SIF_MAIN.INSTANCE.getMainDenom())) {
                return 18;
            } else if (denom.startsWith("ibc/")) {
                return getIbcDecimal(baseData, denom);
            } else {
                Assets assets = baseData.getAsset(denom);
                if (assets != null) {
                    return assets.decimal;
                }
            }
        }
        return 18;
    }

    public static int getSifCoinDecimal(String denom) {
        if (denom.equalsIgnoreCase(TOKEN_SIF)) {
            return 18;
        } else if (denom.equalsIgnoreCase("cusdt")) {
            return 6;
        } else if (denom.equalsIgnoreCase("cusdc")) {
            return 6;
        } else if (denom.equalsIgnoreCase("csrm")) {
            return 6;
        } else if (denom.equalsIgnoreCase("cwscrt")) {
            return 6;
        } else if (denom.equalsIgnoreCase("ccro")) {
            return 8;
        } else if (denom.equalsIgnoreCase("cwbtc")) {
            return 8;
        }
        return 18;
    }

    public static int getGBridgeCoinDecimal(BaseData baseData, String denom) {
        if (denom.equalsIgnoreCase(GRABRIDGE_MAIN.INSTANCE.getMainDenom())) {
            return 6;
        } else if (denom.startsWith("ibc/")) {
            return getIbcDecimal(baseData, denom);
        } else {
            final Assets assets = baseData.getAsset(denom);
            if (assets != null) {
                return assets.decimal;
            }
        }
        return 6;
    }

    public static int getGBridgeCoinDecimal(String denom) {
        if (denom.equalsIgnoreCase(GRABRIDGE_MAIN.INSTANCE.getMainDenom())) {
            return 6;
        } else if (denom.equalsIgnoreCase("gravity0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2")) {
            return 18;
        } else if (denom.equalsIgnoreCase("gravity0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48")) {
            return 6;
        } else if (denom.equalsIgnoreCase("gravity0x6b175474e89094c44da98b954eedeac495271d0f")) {
            return 18;
        }
        return 18;
    }

    public static int getCosmosCoinDecimal(BaseData baseData, String denom) {
        if (denom.equalsIgnoreCase(COSMOS_MAIN.INSTANCE.getMainDenom())) {
            return 6;
        } else if (denom.startsWith("pool")) {
            Liquidity.Pool poolInfo = baseData.getGravityPoolByDenom(denom);
            if (poolInfo != null) {
                return 6;
            }
        } else if (denom.startsWith("ibc/")) {
            return getIbcDecimal(baseData, denom);
        }
        return 6;
    }

    public static int getOsmosisCoinDecimal(BaseData baseData, String denom) {
        if (denom != null) {
            if (denom.equalsIgnoreCase(OSMOSIS_MAIN.INSTANCE.getMainDenom()) || denom.equalsIgnoreCase(TOKEN_ION)) {
                return 6;
            } else if (denom.startsWith("gamm/pool/")) {
                return 18;
            } else if (denom.startsWith("ibc/")) {
                return getIbcDecimal(baseData, denom);
            }
        }
        return 6;
    }

    public static int getInjCoinDecimal(BaseData baseData, String denom) {
        if (denom != null) {
            if (denom.equalsIgnoreCase(INJ_MAIN.INSTANCE.getMainDenom())) {
                return 18;
            } else if (denom.startsWith("ibc/")) {
                return getIbcDecimal(baseData, denom);
            } else {
                Assets assets = baseData.getAsset(denom);
                if (assets != null) {
                    return assets.decimal;
                }
            }
        }
        return 18;
    }

    public static int getIbcDecimal(BaseData baseData, String denom) {
        IbcToken ibcToken = baseData.getIbcToken(denom.replaceAll("ibc/", ""));
        if (ibcToken != null && ibcToken.auth) {
            return ibcToken.decimal;
        } else {
            return 6;
        }
    }

    /**
     * Token Name
     */
    public static String dpCosmosTokenName(BaseData baseData, String denom) {
        if (denom.equals(COSMOS_MAIN.INSTANCE.getMainDenom())) {
            return "ATOM";

        } else if (denom.startsWith("pool")) {
            Liquidity.Pool poolInfo = baseData.getGravityPoolByDenom(denom);
            if (poolInfo != null) {
                return "GDEX-" + poolInfo.getId();
            } else {
                return "Unknown";
            }

        } else if (denom.startsWith("ibc/")) {
            IbcToken ibcToken = baseData.getIbcToken(denom.replaceAll("ibc/", ""));
            if (ibcToken != null && ibcToken.auth) {
                if (ibcToken.base_denom.startsWith("cw20:")) {
                    String cAddress = ibcToken.base_denom.replaceAll("cw20:", "");
                    for (Cw20Assets assets : baseData.mCw20Assets) {
                        if (assets.contract_address.equalsIgnoreCase(cAddress)) {
                            return assets.denom.toUpperCase();
                        }
                    }
                } else {
                    return ibcToken.display_denom.toUpperCase();
                }
            } else {
                return "Unknown";
            }
        }
        return denom;
    }

    public static String dpCosmosTokenName(Context c, BaseData baseData, TextView textView, String denom) {
        if (denom.equals(COSMOS_MAIN.INSTANCE.getMainDenom())) {
            textView.setTextColor(ContextCompat.getColor(c, R.color.colorAtom));
            textView.setText("ATOM");

        } else if (denom.startsWith("pool")) {
            textView.setTextColor(ContextCompat.getColor(c, R.color.colorWhite));
            Liquidity.Pool poolInfo = baseData.getGravityPoolByDenom(denom);
            if (poolInfo != null) {
                textView.setText("GDEX-" + poolInfo.getId());
            } else {
                textView.setText(R.string.str_unknown);
            }

        } else if (denom.startsWith("ibc/")) {
            textView.setTextColor(ContextCompat.getColor(c, R.color.colorWhite));
            IbcToken ibcToken = baseData.getIbcToken(denom.replaceAll("ibc/", ""));
            if (ibcToken != null && ibcToken.auth) {
                if (ibcToken.base_denom.startsWith("cw20:")) {
                    String cAddress = ibcToken.base_denom.replaceAll("cw20:", "");
                    for (Cw20Assets assets : baseData.mCw20Assets) {
                        if (assets.contract_address.equalsIgnoreCase(cAddress)) {
                            textView.setText(assets.denom.toUpperCase());
                        }
                    }
                } else {
                    textView.setText(ibcToken.display_denom.toUpperCase());
                }
            } else {
                textView.setText(R.string.str_unknown);
            }

        } else {
            textView.setTextColor(ContextCompat.getColor(c, R.color.colorWhite));
            textView.setText(R.string.str_unknown);
        }
        return denom;
    }

    public static String dpKavaTokenName(Context c, BaseData baseData, TextView textView, String denom) {
        if (denom.equalsIgnoreCase(KAVA_MAIN.INSTANCE.getMainDenom())) {
            textView.setTextColor(ContextCompat.getColor(c, R.color.colorKava));
            textView.setText(R.string.str_kava_c);
        } else if (denom.equalsIgnoreCase(TOKEN_HARD)) {
            textView.setTextColor(ContextCompat.getColor(c, R.color.colorHard));
            textView.setText("HARD");
        } else if (denom.equalsIgnoreCase(TOKEN_USDX)) {
            textView.setTextColor(ContextCompat.getColor(c, R.color.colorUsdx));
            textView.setText("USDX");
        } else if (denom.equalsIgnoreCase(TOKEN_SWP)) {
            textView.setTextColor(ContextCompat.getColor(c, R.color.colorSwp));
            textView.setText("SWP");
        } else if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_BNB)) {
            textView.setTextColor(ContextCompat.getColor(c, R.color.colorWhite));
            textView.setText("BNB");
        } else if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_XRPB) || denom.equalsIgnoreCase("xrbp")) {
            textView.setTextColor(ContextCompat.getColor(c, R.color.colorWhite));
            textView.setText("XRPB");
        } else if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_BUSD)) {
            textView.setTextColor(ContextCompat.getColor(c, R.color.colorWhite));
            textView.setText("BUSD");
        } else if (denom.contains("btc")) {
            textView.setTextColor(ContextCompat.getColor(c, R.color.colorWhite));
            textView.setText("BTCB");
        } else if (denom.startsWith("ibc/")) {
            textView.setTextColor(ContextCompat.getColor(c, R.color.colorWhite));
            IbcToken ibcToken = baseData.getIbcToken(denom.replaceAll("ibc/", ""));
            if (ibcToken != null && ibcToken.auth) {
                if (ibcToken.base_denom.startsWith("cw20:")) {
                    String cAddress = ibcToken.base_denom.replaceAll("cw20:", "");
                    for (Cw20Assets assets : baseData.mCw20Assets) {
                        if (assets.contract_address.equalsIgnoreCase(cAddress)) {
                            textView.setText(assets.denom.toUpperCase());
                        }
                    }
                } else {
                    textView.setText(ibcToken.display_denom.toUpperCase());
                }
            } else {
                textView.setText(R.string.str_unknown);
            }
        }
        return denom;
    }

    public static String getKavaTokenName(BaseData baseData, String denom) {
        if (denom.equalsIgnoreCase(TOKEN_KAVA)) {
            return "KAVA";
        } else if (denom.equalsIgnoreCase(TOKEN_HARD)) {
            return "HARD";
        } else if (denom.equalsIgnoreCase(TOKEN_SWP)) {
            return "SWP";
        } else if (denom.equalsIgnoreCase(TOKEN_USDX)) {
            return "USDX";

        } else if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_BNB)) {
            return "BNB";

        } else if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_XRPB)) {
            return "XRPB";

        } else if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_BUSD)) {
            return "BUSD";

        } else if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_BTCB)) {
            return "BTCB";

        } else if (denom.equalsIgnoreCase("btch")) {
            return "BTCH";

        } else if (denom.startsWith("ibc/")) {
            IbcToken ibcToken = baseData.getIbcToken(denom.replaceAll("ibc/", ""));
            if (ibcToken != null && ibcToken.auth) {
                if (ibcToken.base_denom.startsWith("cw20:")) {
                    String cAddress = ibcToken.base_denom.replaceAll("cw20:", "");
                    for (Cw20Assets assets : baseData.mCw20Assets) {
                        if (assets.contract_address.equalsIgnoreCase(cAddress)) {
                            return assets.denom.toUpperCase();
                        }
                    }
                } else {
                    return ibcToken.display_denom.toUpperCase();
                }
            } else {
                return "Unknown";
            }
        }
        return denom.toUpperCase();
    }

    public static String getKavaBaseDenom(BaseData baseData, String denom) {
        if (denom.startsWith("ibc/")) {
            IbcToken ibcToken = baseData.getIbcToken(denom.replaceAll("ibc/", ""));
            if (ibcToken != null && ibcToken.auth) {
                if (ibcToken.base_denom.startsWith("cw20:")) {
                    String cAddress = ibcToken.base_denom.replaceAll("cw20:", "");
                    for (Cw20Assets assets : baseData.mCw20Assets) {
                        if (assets.contract_address.equalsIgnoreCase(cAddress)) {
                            return assets.denom;
                        }
                    }
                } else {
                    return ibcToken.base_denom.toUpperCase();
                }
            } else {
                return denom;
            }
        } else if (denom.equalsIgnoreCase(TOKEN_KAVA)) {
            return TOKEN_KAVA;
        } else if (denom.equalsIgnoreCase(TOKEN_HARD)) {
            return TOKEN_HARD;
        } else if (denom.equalsIgnoreCase(TOKEN_USDX)) {
            return TOKEN_USDX;
        } else if (denom.equalsIgnoreCase(TOKEN_SWP)) {
            return TOKEN_SWP;
        } else if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_BNB)) {
            return "bnb";
        } else if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_XRPB)) {
            return "xrp";
        } else if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_BUSD)) {
            return "busd";
        } else if (denom.contains("btc")) {
            return "btc";
        }
        return denom;
    }

    public static String dpOsmosisTokenName(BaseData baseData, String denom) {
        if (denom.equals(TOKEN_OSMOSIS)) {
            return "OSMO";

        } else if (denom.equals(TOKEN_ION)) {
            return "ION";

        } else if (denom.startsWith("gamm/pool/")) {
            String[] split = denom.split("/");
            return "GAMM-" + split[split.length - 1];

        } else if (denom.startsWith("ibc/")) {
            IbcToken ibcToken = baseData.getIbcToken(denom.replaceAll("ibc/", ""));
            if (ibcToken != null && ibcToken.auth) {
                if (ibcToken.base_denom.startsWith("cw20:")) {
                    String cAddress = ibcToken.base_denom.replaceAll("cw20:", "");
                    for (Cw20Assets assets : baseData.mCw20Assets) {
                        if (assets.contract_address.equalsIgnoreCase(cAddress)) {
                            return assets.denom;
                        }
                    }
                } else {
                    return ibcToken.display_denom.toUpperCase();
                }
            } else {
                return "Unknown";
            }
        }
        return denom;
    }

    public static String dpOsmosisTokenName(Context c, BaseData baseData, TextView textView, String denom) {
        if (denom != null) {
            if (denom.equals(TOKEN_OSMOSIS)) {
                textView.setTextColor(ContextCompat.getColor(c, R.color.colorOsmosis));
                textView.setText("OSMO");

            } else if (denom.equals(TOKEN_ION)) {
                textView.setTextColor(ContextCompat.getColor(c, R.color.colorIon));
                textView.setText("ION");

            } else if (denom.startsWith("gamm/pool/")) {
                textView.setTextColor(ContextCompat.getColor(c, R.color.colorWhite));
                String[] split = denom.split("/");
                textView.setText("GAMM-" + split[split.length - 1]);

            } else if (denom.startsWith("ibc/")) {
                textView.setTextColor(ContextCompat.getColor(c, R.color.colorWhite));
                IbcToken ibcToken = baseData.getIbcToken(denom.replaceAll("ibc/", ""));
                if (ibcToken != null && ibcToken.auth) {
                    if (ibcToken.base_denom.startsWith("cw20:")) {
                        String cAddress = ibcToken.base_denom.replaceAll("cw20:", "");
                        for (Cw20Assets assets : baseData.mCw20Assets) {
                            if (assets.contract_address.equalsIgnoreCase(cAddress)) {
                                textView.setText(assets.denom.toUpperCase());
                            }
                        }
                    } else {
                        textView.setText(ibcToken.display_denom.toUpperCase());
                    }
                } else {
                    textView.setText(R.string.str_unknown);
                }
            }
        }
        return denom;
    }

    public static String dpSifTokenName(BaseData baseData, String denom) {
        if (denom.equalsIgnoreCase(TOKEN_SIF)) {
            return "ROWAN";

        } else if (denom.startsWith("ibc/")) {
            IbcToken ibcToken = baseData.getIbcToken(denom.replaceAll("ibc/", ""));
            if (ibcToken != null && ibcToken.auth) {
                if (ibcToken.base_denom.startsWith("cw20:")) {
                    String cAddress = ibcToken.base_denom.replaceAll("cw20:", "");
                    for (Cw20Assets assets : baseData.mCw20Assets) {
                        if (assets.contract_address.equalsIgnoreCase(cAddress)) {
                            return assets.denom.toUpperCase();
                        }
                    }
                } else {
                    return ibcToken.display_denom.toUpperCase();
                }
            } else {
                return "Unknown";
            }

        } else if (denom.startsWith("c")) {
            return denom.substring(1);

        }
        return denom;
    }

    public static String dpSifTokenName(Context c, BaseData baseData, TextView textView, String denom) {
        if (denom != null) {
            if (denom.equals(TOKEN_SIF)) {
                textView.setTextColor(ContextCompat.getColor(c, R.color.colorSif));
                textView.setText("ROWAN");

            } else if (denom.startsWith("c")) {
                textView.setTextColor(ContextCompat.getColor(c, R.color.colorWhite));
                textView.setText(denom.substring(1).toUpperCase());

            } else if (denom.startsWith("ibc/")) {
                textView.setTextColor(ContextCompat.getColor(c, R.color.colorWhite));
                IbcToken ibcToken = baseData.getIbcToken(denom.replaceAll("ibc/", ""));
                if (ibcToken != null && ibcToken.auth) {
                    if (ibcToken.base_denom.startsWith("cw20:")) {
                        String cAddress = ibcToken.base_denom.replaceAll("cw20:", "");
                        for (Cw20Assets assets : baseData.mCw20Assets) {
                            if (assets.contract_address.equalsIgnoreCase(cAddress)) {
                                textView.setText(assets.denom.toUpperCase());
                            }
                        }
                    } else {
                        textView.setText(ibcToken.display_denom.toUpperCase());
                    }
                } else {
                    textView.setText(R.string.str_unknown);
                }

            } else {
                textView.setTextColor(ContextCompat.getColor(c, R.color.colorWhite));
                textView.setText(R.string.str_unknown);
            }
        }
        return denom;
    }

    /**
     * Token Img
     */
    public static void DpCosmosTokenImg(BaseData baseData, ImageView imageView, String denom) {
        if (denom.equalsIgnoreCase(COSMOS_MAIN.INSTANCE.getMainDenom())) {
            Picasso.get().cancelRequest(imageView);
            imageView.setImageResource(COSMOS_MAIN.INSTANCE.getCoinIcon());
        } else if (denom.startsWith("pool")) {
            Liquidity.Pool poolInfo = baseData.getGravityPoolByDenom(denom);
            if (poolInfo != null) {
                imageView.setImageResource(R.drawable.token_gravitydex);
            }
        } else if (denom.startsWith("ibc/")) {
            IbcToken ibcToken = baseData.getIbcToken(denom.replaceAll("ibc/", ""));
            try {
                Picasso.get().load(ibcToken.moniker).fit().placeholder(R.drawable.token_default_ibc).error(R.drawable.token_default_ibc).into(imageView);
            } catch (Exception e) {
            }
        }
    }

    public static void DpOsmosisTokenImg(BaseData baseData, ImageView imageView, String denom) {
        if (denom != null) {
            if (denom.equalsIgnoreCase(OSMOSIS_MAIN.INSTANCE.getMainDenom())) {
                Picasso.get().cancelRequest(imageView);
                imageView.setImageResource(OSMOSIS_MAIN.INSTANCE.getCoinIcon());
            } else if (denom.equalsIgnoreCase(TOKEN_ION)) {
                imageView.setImageResource(R.drawable.token_ion);
            } else if (denom.startsWith("gamm/pool/")) {
                imageView.setImageResource(R.drawable.token_pool);
            } else if (denom.startsWith("ibc/")) {
                IbcToken ibcToken = baseData.getIbcToken(denom.replaceAll("ibc/", ""));
                try {
                    Picasso.get().load(ibcToken.moniker).fit().placeholder(R.drawable.token_default_ibc).error(R.drawable.token_default_ibc).into(imageView);
                } catch (Exception e) {
                }
            }
        }
    }

    public static void DpSifTokenImg(BaseData baseData, ImageView imageView, String denom) {
        if (denom != null) {
            if (denom.equalsIgnoreCase(SIF_MAIN.INSTANCE.getMainDenom())) {
                Picasso.get().cancelRequest(imageView);
                imageView.setImageResource(SIF_MAIN.INSTANCE.getCoinIcon());
            } else if (denom.startsWith("c")) {
                Assets assets = baseData.getAsset(denom);
                if (assets != null) {
                    Picasso.get().load(ASSET_IMG_URL + assets.logo).fit().placeholder(R.drawable.token_ic).error(R.drawable.token_ic).into(imageView);
                }
            } else if (denom.startsWith("ibc/")) {
                IbcToken ibcToken = baseData.getIbcToken(denom.replaceAll("ibc/", ""));
                try {
                    Picasso.get().load(ibcToken.moniker).fit().placeholder(R.drawable.token_default_ibc).error(R.drawable.token_default_ibc).into(imageView);
                } catch (Exception e) {
                }
            }
        }
    }

    public static void DpKavaTokenImg(BaseData baseData, ImageView imageView, String denom) {
        if (denom != null) {
            if (denom.equalsIgnoreCase(KAVA_MAIN.INSTANCE.getMainDenom())) {
                Picasso.get().cancelRequest(imageView);
                imageView.setImageResource(KAVA_MAIN.INSTANCE.getCoinIcon());
            } else if (denom.startsWith("ibc/")) {
                IbcToken ibcToken = baseData.getIbcToken(denom.replaceAll("ibc/", ""));
                if (ibcToken != null) {
                    Picasso.get().load(ibcToken.moniker).fit().placeholder(R.drawable.token_default_ibc).error(R.drawable.token_default_ibc).into(imageView);
                }
            } else {
                Picasso.get().load(KAVA_COIN_IMG_URL + denom + ".png").fit().placeholder(R.drawable.token_ic).error(R.drawable.token_ic).into(imageView);
            }
        }
    }

    // cosmos gravity dex
    public static GDexManager getGDexManager(BaseData baseData, String address) {
        for (GDexManager gDexManager : baseData.mGDexManager) {
            if (gDexManager.address.equalsIgnoreCase(address)) {
                return gDexManager;
            }
        }
        return null;
    }

    public static BigDecimal getLpAmount(BaseData baseData, String address, String denom) {
        BigDecimal result = BigDecimal.ZERO;
        if (getGDexManager(baseData, address) != null) {
            for (Coin coin : getGDexManager(baseData, address).reserve) {
                if (coin.denom.equalsIgnoreCase(denom)) {
                    result = new BigDecimal(coin.amount);
                }
            }
        }
        return result;
    }

    public static BigDecimal getParamGdexPoolValue(BaseData baseData, ChainParam.GdexStatus pool, PriceProvider priceProvider) {
        String coin0Denom = pool.tokenPairs.get(0).denom;
        String coin1Denom = pool.tokenPairs.get(1).denom;
        String coin0BaseDenom = baseData.getBaseDenom(coin0Denom);
        String coin1BaseDenom = baseData.getBaseDenom(coin1Denom);
        BigDecimal coin0Amount = new BigDecimal(pool.tokenPairs.get(0).amount);
        BigDecimal coin1Amount = new BigDecimal(pool.tokenPairs.get(1).amount);
        int coin0Decimal = WUtil.getCosmosCoinDecimal(baseData, coin0Denom);
        int coin1Decimal = WUtil.getCosmosCoinDecimal(baseData, coin1Denom);
        BigDecimal coin0Price = BigDecimal.ZERO;
        BigDecimal coin1Price = BigDecimal.ZERO;
        if (coin0BaseDenom != null) {
            coin0Price = WDp.perUsdValue(baseData, coin0BaseDenom, priceProvider);
        }
        if (coin1BaseDenom != null) {
            coin1Price = WDp.perUsdValue(baseData, coin1BaseDenom, priceProvider);
        }
        BigDecimal coin0Value = coin0Amount.multiply(coin0Price).movePointLeft(coin0Decimal).setScale(2, RoundingMode.DOWN);
        BigDecimal coin1Value = coin1Amount.multiply(coin1Price).movePointLeft(coin1Decimal).setScale(2, RoundingMode.DOWN);
        return coin0Value.add(coin1Value);
    }

    public static BigDecimal getParamGdexLpTokenPerUsdPrice(BaseData baseData, ChainParam.GdexStatus pool, PriceProvider priceProvider) {
        BigDecimal poolValue = getParamGdexPoolValue(baseData, pool, priceProvider);
        BigDecimal totalShare = BigDecimal.ZERO;
        for (Coin coin : baseData.mChainParam.getSupplys()) {
            if (coin.denom.equalsIgnoreCase(pool.pool_token)) {
                totalShare = new BigDecimal(coin.amount);
            }
        }
        return poolValue.divide(totalShare.movePointLeft(6).setScale(24, RoundingMode.DOWN), 18, RoundingMode.DOWN);
    }

    /**
     * About Osmosis
     */
    public static BigDecimal getMyShareLpAmount(BaseActivity baseActivity, BaseData baseData, BalancerPool.Pool pool, String denom) {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal myShare = baseActivity.getBalance("gamm/pool/" + pool.getId());
        String totalLpCoin = "";
        if (pool.getPoolAssets(0).getToken().getDenom().equalsIgnoreCase(denom)) {
            totalLpCoin = pool.getPoolAssets(0).getToken().getAmount();
        } else {
            totalLpCoin = pool.getPoolAssets(1).getToken().getAmount();
        }
        result = new BigDecimal(totalLpCoin).multiply(myShare).divide(new BigDecimal(pool.getTotalShares().getAmount()), 18, RoundingMode.DOWN);
        return result;
    }

    public static List<GaugeOuterClass.Gauge> getGaugesByPoolId(long poolId, List<QueryOuterClass.IncentivizedPool> incentivizedPools, List<GaugeOuterClass.Gauge> allGauges) {
        ArrayList<Long> gaugeIds = new ArrayList<Long>();
        ArrayList<GaugeOuterClass.Gauge> result = new ArrayList<GaugeOuterClass.Gauge>();
        for (QueryOuterClass.IncentivizedPool pool : incentivizedPools) {
            if (pool.getPoolId() == poolId) {
                gaugeIds.add(pool.getGaugeId());
            }
        }
        for (GaugeOuterClass.Gauge gauge : allGauges) {
            if (gaugeIds.contains(gauge.getId())) {
                result.add(gauge);
            }
        }
        return result;
    }

    public static ArrayList<Lock.PeriodLock> getLockupByPoolId(long poolId, List<Lock.PeriodLock> lockups) {
        ArrayList<Lock.PeriodLock> result = new ArrayList<Lock.PeriodLock>();
        for (Lock.PeriodLock lockup : lockups) {
            Coin lpCoin = new Coin(lockup.getCoins(0).getDenom(), lockup.getCoins(0).getAmount());
            if (lpCoin.osmosisAmmPoolId() == poolId) {
                result.add(lockup);
            }
        }
        return result;
    }

    public static BigDecimal getOsmoLpTokenPerUsdPrice(BaseData baseData, BalancerPool.Pool pool, PriceProvider priceProvider) {
        try {
            BigDecimal totalShare = (new BigDecimal(pool.getTotalShares().getAmount())).movePointLeft(18).setScale(18, RoundingMode.DOWN);
            return getPoolValue(baseData, pool, priceProvider).divide(totalShare, 18, RoundingMode.DOWN);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public static BigDecimal getPoolValue(BaseData baseData, BalancerPool.Pool pool, PriceProvider priceProvider) {
        Coin coin0 = new Coin(pool.getPoolAssets(0).getToken().getDenom(), pool.getPoolAssets(0).getToken().getAmount());
        Coin coin1 = new Coin(pool.getPoolAssets(1).getToken().getDenom(), pool.getPoolAssets(1).getToken().getAmount());
        BigDecimal coin0Value = WDp.usdValue(baseData, baseData.getBaseDenom(coin0.denom), new BigDecimal(coin0.amount), WUtil.getOsmosisCoinDecimal(baseData, coin0.denom), priceProvider);
        BigDecimal coin1Value = WDp.usdValue(baseData, baseData.getBaseDenom(coin1.denom), new BigDecimal(coin1.amount), WUtil.getOsmosisCoinDecimal(baseData, coin1.denom), priceProvider);
        return coin0Value.add(coin1Value);
    }

    public static BigDecimal getNextIncentiveAmount(List<GaugeOuterClass.Gauge> gauges, int position) {
        if (gauges.size() != 3) {
            return BigDecimal.ZERO;
        }
        BigDecimal incentive1Day = BigDecimal.ZERO;
        BigDecimal incentive7Day = BigDecimal.ZERO;
        BigDecimal incentive14Day = BigDecimal.ZERO;
        if (gauges.get(0).getDistributedCoinsCount() == 0) {
            return BigDecimal.ZERO;
        } else {
            for (CoinOuterClass.Coin coin : gauges.get(0).getCoinsList()) {
                if (coin.getDenom().equalsIgnoreCase(gauges.get(0).getDistributedCoins(0).getDenom())) {
                    incentive1Day = new BigDecimal(coin.getAmount()).subtract(new BigDecimal(gauges.get(0).getDistributedCoins(0).getAmount()));
                }
            }
        }
        if (gauges.get(1).getDistributedCoinsCount() == 0) {
            return BigDecimal.ZERO;
        } else {
            for (CoinOuterClass.Coin coin : gauges.get(1).getCoinsList()) {
                if (coin.getDenom().equalsIgnoreCase(gauges.get(1).getDistributedCoins(0).getDenom())) {
                    incentive7Day = new BigDecimal(coin.getAmount()).subtract(new BigDecimal(gauges.get(1).getDistributedCoins(0).getAmount()));
                }
            }
        }
        if (gauges.get(2).getDistributedCoinsCount() == 0) {
            return BigDecimal.ZERO;
        } else {
            for (CoinOuterClass.Coin coin : gauges.get(2).getCoinsList()) {
                if (coin.getDenom().equalsIgnoreCase(gauges.get(2).getDistributedCoins(0).getDenom())) {
                    incentive14Day = new BigDecimal(coin.getAmount()).subtract(new BigDecimal(gauges.get(2).getDistributedCoins(0).getAmount()));
                }
            }
        }
        if (position == 0) {
            return incentive1Day;
        } else if (position == 1) {
            return incentive1Day.add(incentive7Day);
        } else {
            return incentive1Day.add(incentive7Day).add(incentive14Day);
        }
    }

    public static BigDecimal getPoolArp(BaseData baseData, BalancerPool.Pool pool, List<GaugeOuterClass.Gauge> gauges, int position, PriceProvider priceProvider) {
        BigDecimal poolValue = getPoolValue(baseData, pool, priceProvider);
        BigDecimal incentiveAmount = getNextIncentiveAmount(gauges, position);
        BigDecimal incentiveValue = WDp.usdValue(baseData, baseData.getBaseDenom(TOKEN_OSMOSIS), incentiveAmount, WUtil.getOsmosisCoinDecimal(baseData, TOKEN_OSMOSIS), priceProvider);
        return incentiveValue.multiply(new BigDecimal("36500")).divide(poolValue, 12, RoundingMode.DOWN);
    }

    /**
     * About Sif
     */
    public static BigDecimal getNativeAmount(sifnode.clp.v1.Types.Pool pool) {
        return new BigDecimal(pool.getNativeAssetBalance());
    }

    public static BigDecimal getExternalAmount(sifnode.clp.v1.Types.Pool pool) {
        return new BigDecimal(pool.getExternalAssetBalance());
    }

    public static BigDecimal getUnitAmount(sifnode.clp.v1.Types.Pool pool) {
        return new BigDecimal(pool.getPoolUnits());
    }

    public static BigDecimal getPoolLpAmount(sifnode.clp.v1.Types.Pool pool, String denom) {
        if (denom != null) {
            if (denom.equals(TOKEN_SIF)) {
                return getNativeAmount(pool);
            } else {
                return getExternalAmount(pool);
            }
        }
        return BigDecimal.ONE;
    }

    public static BigDecimal getSifPoolValue(BaseData baseData, sifnode.clp.v1.Types.Pool pool, PriceProvider priceProvider) {
        int rowanDecimal = getSifCoinDecimal(baseData, TOKEN_SIF);
        BigDecimal rowanAmount = new BigDecimal(pool.getNativeAssetBalance());
        BigDecimal rowanPrice = WDp.perUsdValue(baseData, TOKEN_SIF, priceProvider);

        int externalDecimal = getSifCoinDecimal(baseData, pool.getExternalAsset().getSymbol());
        BigDecimal externalAmount = new BigDecimal(pool.getExternalAssetBalance());
        String exteranlBaseDenom = baseData.getBaseDenom(pool.getExternalAsset().getSymbol());
        BigDecimal exteranlPrice = WDp.perUsdValue(baseData, exteranlBaseDenom, priceProvider);

        BigDecimal rowanValue = rowanAmount.multiply(rowanPrice).movePointLeft(rowanDecimal);
        BigDecimal externalValue = externalAmount.multiply(exteranlPrice).movePointLeft(externalDecimal).setScale(2, RoundingMode.DOWN);
        return rowanValue.add(externalValue);
    }

    public static BigDecimal getSifMyShareValue(BaseData baseData, sifnode.clp.v1.Types.Pool pool, Querier.LiquidityProviderRes myLp, PriceProvider priceProvider) {
        BigDecimal poolValue = getSifPoolValue(baseData, pool, priceProvider);
        BigDecimal totalUnit = new BigDecimal(pool.getPoolUnits());
        BigDecimal myUnit = new BigDecimal(myLp.getLiquidityProvider().getLiquidityProviderUnits());
        return poolValue.multiply(myUnit).divide(totalUnit, 2, RoundingMode.DOWN);
    }

    /**
     * About NFT
     */
    public static String getNftDescription(String data) {
        String description = "";
        try {
            JSONObject json = new JSONObject(data);
            description = json.getJSONObject("body").getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject json = new JSONObject(data);
            description = json.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return description;
    }

    public static String getNftIssuer(String data) {
        String issuer = "";
        try {
            JSONObject json = new JSONObject(data);
            issuer = json.getJSONObject("body").getString("issuerAddr");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject json = new JSONObject(data);
            issuer = json.getString("issuerAddr");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return issuer;
    }

    public static String getNftImgUrl(String data) {
        String url = "";
        try {
            JSONObject json = new JSONObject(data);
            url = json.getString("image");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject json = new JSONObject(data);
            url = json.getString("imgurl");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static ArrayList<BaseChain> getDesmosAirDropChains() {
        ArrayList<BaseChain> result = new ArrayList<>();
        result.add(COSMOS_MAIN.INSTANCE);
        result.add(OSMOSIS_MAIN.INSTANCE);
        result.add(AKASH_MAIN.INSTANCE);
        result.add(BAND_MAIN.INSTANCE);
        result.add(CRYPTO_MAIN.INSTANCE);
        result.add(JUNO_MAIN.INSTANCE);
        result.add(KAVA_MAIN.INSTANCE);
        result.add(EMONEY_MAIN.INSTANCE);
        result.add(REGEN_MAIN.INSTANCE);
        return result;
    }

    public static String getDesmosPrefix(BaseChain baseChain) {
        if (baseChain.equals(COSMOS_MAIN.INSTANCE)) {
            return "cosmos";
        } else if (baseChain.equals(OSMOSIS_MAIN.INSTANCE)) {
            return "osmo";
        } else if (baseChain.equals(AKASH_MAIN.INSTANCE)) {
            return "akash";
        } else if (baseChain.equals(BAND_MAIN.INSTANCE)) {
            return "band";
        } else if (baseChain.equals(CRYPTO_MAIN.INSTANCE)) {
            return "cro";
        } else if (baseChain.equals(JUNO_MAIN.INSTANCE)) {
            return "juno";
        } else if (baseChain.equals(KAVA_MAIN.INSTANCE)) {
            return "kava";
        } else if (baseChain.equals(EMONEY_MAIN.INSTANCE)) {
            return "emoney";
        } else if (baseChain.equals(REGEN_MAIN.INSTANCE)) {
            return "regen";
        } else {
            return "";
        }
    }

    public static String getDesmosConfig(BaseChain baseChain) {
        if (baseChain.equals(COSMOS_MAIN.INSTANCE)) {
            return "cosmos";
        } else if (baseChain.equals(OSMOSIS_MAIN.INSTANCE)) {
            return "osmosis";
        } else if (baseChain.equals(AKASH_MAIN.INSTANCE)) {
            return "akash";
        } else if (baseChain.equals(BAND_MAIN.INSTANCE)) {
            return "band";
        } else if (baseChain.equals(CRYPTO_MAIN.INSTANCE)) {
            return "cro";
        } else if (baseChain.equals(JUNO_MAIN.INSTANCE)) {
            return "juno";
        } else if (baseChain.equals(KAVA_MAIN.INSTANCE)) {
            return "kava";
        } else if (baseChain.equals(EMONEY_MAIN.INSTANCE)) {
            return "emoney";
        } else if (baseChain.equals(REGEN_MAIN.INSTANCE)) {
            return "regen";
        } else {
            return "";
        }
    }

    /**
     * About KAVA
     */

    //CDP
    public static BigDecimal getDpStabilityFee(Genesis.CollateralParam collateralParam) {
        return (new BigDecimal(collateralParam.getStabilityFee()).movePointLeft(18).subtract(BigDecimal.ONE)).multiply(new BigDecimal("31536000")).movePointRight(2);
    }

    public static BigDecimal getDpLiquidationPenalty(Genesis.CollateralParam collateralParam) {
        return new BigDecimal(collateralParam.getLiquidationPenalty()).movePointRight(2);
    }

    public static BigDecimal getRawDebtAmount(kava.cdp.v1beta1.QueryOuterClass.CDPResponse myCdp) {
        return new BigDecimal(myCdp.getPrincipal().getAmount()).add(new BigDecimal(myCdp.getAccumulatedFees().getAmount()));
    }

    public static BigDecimal getEstimatedTotalFee(Context c, kava.cdp.v1beta1.QueryOuterClass.CDPResponse myCdp, Genesis.CollateralParam collateralParam) {
        BigDecimal hiddenFeeValue = WDp.getCdpGrpcHiddenFee(c, getRawDebtAmount(myCdp), collateralParam, myCdp);
        return new BigDecimal(myCdp.getAccumulatedFees().getAmount()).add(hiddenFeeValue);
    }

    public static BigDecimal getEstimatedTotalDebt(Context c, kava.cdp.v1beta1.QueryOuterClass.CDPResponse myCdp, Genesis.CollateralParam cParam) {
        BigDecimal hiddenFeeValue = WDp.getCdpGrpcHiddenFee(c, getRawDebtAmount(myCdp), cParam, myCdp);
        return getRawDebtAmount(myCdp).add(hiddenFeeValue);
    }

    public static BigDecimal getLiquidationPrice(Context c, BaseData baseData, kava.cdp.v1beta1.QueryOuterClass.CDPResponse myCdp, Genesis.CollateralParam cParam) {
        int denomCDecimal = WUtil.getKavaCoinDecimal(baseData, myCdp.getCollateral().getDenom());
        int denomPDecimal = WUtil.getKavaCoinDecimal(baseData, myCdp.getPrincipal().getDenom());
        BigDecimal collateralAmount = new BigDecimal(myCdp.getCollateral().getAmount()).movePointLeft(denomCDecimal);
        BigDecimal estimatedDebtAmount = getEstimatedTotalDebt(c, myCdp, cParam).multiply(new BigDecimal(cParam.getLiquidationRatio())).movePointLeft(denomPDecimal);
        return estimatedDebtAmount.divide(collateralAmount, denomPDecimal, BigDecimal.ROUND_DOWN).movePointLeft(18);
    }

    public static BigDecimal getWithdrawableAmount(Context c, BaseData baseData, kava.cdp.v1beta1.QueryOuterClass.CDPResponse myCdp, Genesis.CollateralParam cParam, BigDecimal price, BigDecimal selfDeposit) {
        int denomCDecimal = WUtil.getKavaCoinDecimal(baseData, cParam.getDenom());
        int denomPDecimal = WUtil.getKavaCoinDecimal(baseData, cParam.getDebtLimit().getDenom());
        BigDecimal cValue = new BigDecimal(myCdp.getCollateralValue().getAmount());
        BigDecimal minCValue = getEstimatedTotalDebt(c, myCdp, cParam).multiply(new BigDecimal(cParam.getLiquidationRatio()).movePointLeft(18)).divide(new BigDecimal("0.95"), 0, BigDecimal.ROUND_DOWN);
        BigDecimal maxWithdrawableValue = cValue.subtract(minCValue);
        BigDecimal maxWithdrawableAmount = maxWithdrawableValue.movePointLeft(denomPDecimal - denomCDecimal).divide(price, 0, RoundingMode.DOWN);
        if (maxWithdrawableAmount.compareTo(selfDeposit) > 0) {
            maxWithdrawableAmount = selfDeposit;
        }
        if (maxWithdrawableAmount.compareTo(BigDecimal.ZERO) <= 0) {
            maxWithdrawableAmount = BigDecimal.ZERO;
        }
        return maxWithdrawableAmount;
    }

    public static BigDecimal getMoreLoanableAmount(Context c, kava.cdp.v1beta1.QueryOuterClass.CDPResponse myCdp, Genesis.CollateralParam cParam) {
        BigDecimal maxDebtValue = new BigDecimal(myCdp.getCollateralValue().getAmount()).divide(new BigDecimal(cParam.getLiquidationRatio()).movePointLeft(18), 0, RoundingMode.DOWN);
        maxDebtValue = maxDebtValue.multiply(new BigDecimal("0.95")).setScale(0, RoundingMode.DOWN);

        maxDebtValue = maxDebtValue.subtract(getEstimatedTotalDebt(c, myCdp, cParam));
        if (maxDebtValue.compareTo(BigDecimal.ZERO) <= 0) {
            maxDebtValue = BigDecimal.ZERO;
        }
        return maxDebtValue;

    }

    //Hard
    public static String getSpotMarketId(kava.hard.v1beta1.Hard.Params Hardparams, String denom) {
        for (Hard.MoneyMarket moneyMarket : Hardparams.getMoneyMarketsList()) {
            if (moneyMarket.getDenom().equalsIgnoreCase(denom)) {
                return moneyMarket.getSpotMarketId();
            }
        }
        return null;
    }

    public static BigDecimal getLTV(kava.hard.v1beta1.Hard.Params Hardparams, String denom) {
        BigDecimal result = BigDecimal.ZERO;
        for (Hard.MoneyMarket moneyMarket : Hardparams.getMoneyMarketsList()) {
            if (moneyMarket.getDenom().equalsIgnoreCase(denom)) {
                result = new BigDecimal(moneyMarket.getBorrowLimit().getLoanToValue()).movePointLeft(18);
            }
        }
        return result;
    }

    public static Hard.MoneyMarket getHardMoneyMarket(Hard.Params params, String denom) {
        for (Hard.MoneyMarket market : params.getMoneyMarketsList()) {
            if (market.getDenom().equalsIgnoreCase(denom)) {
                return market;
            }
        }
        return null;
    }

    public static BigDecimal getKavaPrice(BaseData baseData, String denom) {
        BigDecimal result;
        if (denom.equals("usdx")) {
            result = BigDecimal.ONE;
        } else {
            Hard.Params hardParam = baseData.mHardParams;
            result = baseData.getKavaOraclePrice(WUtil.getSpotMarketId(hardParam, denom));
        }
        return result;
    }

    public static BigDecimal getHardSuppliedAmountByDenom(Context context, BaseData baseData, String denom, ArrayList<kava.hard.v1beta1.QueryOuterClass.DepositResponse> myDeposit) {
        BigDecimal myDepositAmount = BigDecimal.ZERO;
        if (myDeposit != null && myDeposit.size() > 0) {
            for (CoinOuterClass.Coin coin : myDeposit.get(0).getAmountList()) {
                if (coin.getDenom().equalsIgnoreCase(denom)) {
                    myDepositAmount = new BigDecimal(coin.getAmount());
                }
            }
        }
        return myDepositAmount;
    }

    public static BigDecimal getHardSuppliedValueByDenom(Context context, BaseData baseData, String denom, ArrayList<kava.hard.v1beta1.QueryOuterClass.DepositResponse> myDeposit) {
        final BigDecimal denomPrice = getKavaPrice(baseData, denom);
        final int decimal = WUtil.getKavaCoinDecimal(baseData, denom);
        return (getHardSuppliedAmountByDenom(context, baseData, denom, myDeposit)).movePointLeft(decimal).multiply(denomPrice);
    }

    public static BigDecimal getHardBorrowedAmountByDenom(Context context, BaseData baseData, String denom, ArrayList<kava.hard.v1beta1.QueryOuterClass.BorrowResponse> myBorrow) {
        BigDecimal myBorrowedAmount = BigDecimal.ZERO;
        if (myBorrow != null && myBorrow.size() > 0) {
            for (CoinOuterClass.Coin coin : myBorrow.get(0).getAmountList()) {
                if (coin.getDenom().equalsIgnoreCase(denom)) {
                    myBorrowedAmount = new BigDecimal(coin.getAmount());
                }
            }
        }
        return myBorrowedAmount;
    }

    public static BigDecimal getHardBorrowedValueByDenom(Context context, BaseData baseData, String denom, ArrayList<kava.hard.v1beta1.QueryOuterClass.BorrowResponse> myBorrow) {
        final BigDecimal denomPrice = getKavaPrice(baseData, denom);
        final int decimal = WUtil.getKavaCoinDecimal(baseData, denom);
        return (getHardBorrowedAmountByDenom(context, baseData, denom, myBorrow)).movePointLeft(decimal).multiply(denomPrice);

    }

    public static BigDecimal getHardBorrowableAmountByDenom(Context context, BaseData baseData, String denom, ArrayList<kava.hard.v1beta1.QueryOuterClass.DepositResponse> myDeposit, ArrayList<kava.hard.v1beta1.QueryOuterClass.BorrowResponse> myBorrow, ArrayList<Coin> moduleCoins, ArrayList<CoinOuterClass.Coin> reserveCoin) {
        BigDecimal totalLTVValue = BigDecimal.ZERO;
        BigDecimal totalBorrowedValue = BigDecimal.ZERO;
        BigDecimal totalBorrowAbleValue = BigDecimal.ZERO;
        BigDecimal totalBorrowAbleAmount = BigDecimal.ZERO;

        BigDecimal SystemBorrowableAmount = BigDecimal.ZERO;
        BigDecimal moduleAmount = BigDecimal.ZERO;
        BigDecimal reserveAmount = BigDecimal.ZERO;

        final Hard.Params hardParam = baseData.mHardParams;
        final Hard.MoneyMarket hardMoneyMarket = WUtil.getHardMoneyMarket(hardParam, denom);
        final BigDecimal denomPrice = getKavaPrice(baseData, denom);
        final int decimal = WUtil.getKavaCoinDecimal(baseData, denom);

        if (myDeposit != null && myDeposit.size() > 0) {
            for (CoinOuterClass.Coin coin : myDeposit.get(0).getAmountList()) {
                int innnerDecimal = WUtil.getKavaCoinDecimal(baseData, coin.getDenom());
                BigDecimal LTV = WUtil.getLTV(hardParam, coin.getDenom());
                BigDecimal depositValue = BigDecimal.ZERO;
                BigDecimal ltvValue = BigDecimal.ZERO;
                if (coin.getDenom().equalsIgnoreCase("usdx")) {
                    depositValue = (new BigDecimal(coin.getAmount())).movePointLeft(innnerDecimal);

                } else {
                    BigDecimal innerPrice = getKavaPrice(baseData, coin.getDenom());
                    depositValue = (new BigDecimal(coin.getAmount())).movePointLeft(innnerDecimal).multiply(innerPrice);

                }
                ltvValue = depositValue.multiply(LTV);
                totalLTVValue = totalLTVValue.add(ltvValue);
            }
        }

        if (myBorrow != null && myBorrow.size() > 0) {
            for (CoinOuterClass.Coin coin : myBorrow.get(0).getAmountList()) {
                int innnerDecimal = WUtil.getKavaCoinDecimal(baseData, coin.getDenom());
                BigDecimal borrowedValue = BigDecimal.ZERO;
                if (coin.getDenom().equalsIgnoreCase("usdx")) {
                    borrowedValue = (new BigDecimal(coin.getAmount())).movePointLeft(innnerDecimal);

                } else {
                    BigDecimal innerPrice = getKavaPrice(baseData, coin.getDenom());
                    borrowedValue = (new BigDecimal(coin.getAmount())).movePointLeft(innnerDecimal).multiply(innerPrice);

                }
                totalBorrowedValue = totalBorrowedValue.add(borrowedValue);
            }
        }
        totalBorrowAbleValue = (totalLTVValue.subtract(totalBorrowedValue)).max(BigDecimal.ZERO);
        totalBorrowAbleAmount = totalBorrowAbleValue.movePointRight(decimal).divide(denomPrice, decimal, RoundingMode.DOWN);

        if (moduleCoins != null) {
            for (Coin coin : moduleCoins) {
                if (coin.denom.equals(denom)) {
                    moduleAmount = new BigDecimal(coin.amount);
                }
            }
        }
        if (reserveCoin != null) {
            for (CoinOuterClass.Coin coin : reserveCoin) {
                if (coin.getDenom().equalsIgnoreCase(denom)) {
                    reserveAmount = new BigDecimal(coin.getAmount());
                }
            }
        }
        BigDecimal moduleBorrowable = moduleAmount.subtract(reserveAmount);
        if (hardMoneyMarket.getBorrowLimit().getHasMaxLimit()) {
            SystemBorrowableAmount = new BigDecimal(hardMoneyMarket.getBorrowLimit().getMaximumLimit()).min(moduleBorrowable);
        } else {
            SystemBorrowableAmount = moduleBorrowable;
        }
        BigDecimal finalBorrowableAmount = totalBorrowAbleAmount.min(SystemBorrowableAmount);

        return finalBorrowableAmount;
    }

    public static BigDecimal getHardBorrowableValueByDenom(Context context, BaseData baseData, String denom, ArrayList<kava.hard.v1beta1.QueryOuterClass.DepositResponse> myDeposit, ArrayList<kava.hard.v1beta1.QueryOuterClass.BorrowResponse> myBorrow, ArrayList<Coin> moduleCoins, ArrayList<CoinOuterClass.Coin> reserveCoin) {
        BigDecimal totalLTVValue = BigDecimal.ZERO;
        BigDecimal totalBorrowedValue = BigDecimal.ZERO;
        BigDecimal totalBorrowAbleValue;

        BigDecimal SystemBorrowableAmount;
        BigDecimal SystemBorrowableValue;
        BigDecimal moduleAmount = BigDecimal.ZERO;
        BigDecimal reserveAmount = BigDecimal.ZERO;

        final Hard.Params hardParam = baseData.mHardParams;
        final Hard.MoneyMarket hardMoneyMarket = WUtil.getHardMoneyMarket(hardParam, denom);
        final BigDecimal denomPrice = getKavaPrice(baseData, denom);
        final int decimal = WUtil.getKavaCoinDecimal(baseData, denom);

        if (myDeposit != null && myDeposit.size() > 0) {
            for (CoinOuterClass.Coin coin : myDeposit.get(0).getAmountList()) {
                int innnerDecimal = WUtil.getKavaCoinDecimal(baseData, coin.getDenom());
                BigDecimal LTV = WUtil.getLTV(hardParam, coin.getDenom());
                BigDecimal depositValue;

                if (coin.getDenom().equalsIgnoreCase("usdx")) {
                    depositValue = (new BigDecimal(coin.getAmount())).movePointLeft(innnerDecimal);

                } else {
                    BigDecimal innerPrice = getKavaPrice(baseData, coin.getDenom());
                    depositValue = (new BigDecimal(coin.getAmount())).movePointLeft(innnerDecimal).multiply(innerPrice);

                }
                BigDecimal ltvValue = depositValue.multiply(LTV);
                totalLTVValue = totalLTVValue.add(ltvValue);
            }
        }

        if (myBorrow != null && myBorrow.size() > 0) {
            for (CoinOuterClass.Coin coin : myBorrow.get(0).getAmountList()) {
                int innnerDecimal = WUtil.getKavaCoinDecimal(baseData, coin.getDenom());
                BigDecimal borrowedValue;
                if (coin.getDenom().equals("usdx")) {
                    borrowedValue = (new BigDecimal(coin.getAmount())).movePointLeft(innnerDecimal);

                } else {
                    BigDecimal innerPrice = getKavaPrice(baseData, coin.getDenom());
                    borrowedValue = (new BigDecimal(coin.getAmount())).movePointLeft(innnerDecimal).multiply(innerPrice);

                }
                totalBorrowedValue = totalBorrowedValue.add(borrowedValue);
            }
        }
        totalBorrowAbleValue = (totalLTVValue.subtract(totalBorrowedValue)).max(BigDecimal.ZERO);

        if (moduleCoins != null) {
            for (Coin coin : moduleCoins) {
                if (coin.denom.equals(denom)) {
                    moduleAmount = new BigDecimal(coin.amount);
                }
            }
        }
        if (reserveCoin != null) {
            for (CoinOuterClass.Coin coin : reserveCoin) {
                if (coin.getDenom().equals(denom)) {
                    reserveAmount = new BigDecimal(coin.getAmount());
                }
            }
        }
        BigDecimal moduleBorrowable = moduleAmount.subtract(reserveAmount);
        if (hardMoneyMarket.getBorrowLimit().getHasMaxLimit()) {
            SystemBorrowableAmount = new BigDecimal(hardMoneyMarket.getBorrowLimit().getMaximumLimit()).min(moduleBorrowable);
        } else {
            SystemBorrowableAmount = moduleBorrowable;
        }
        SystemBorrowableValue = SystemBorrowableAmount.movePointLeft(decimal).multiply(denomPrice);

        return totalBorrowAbleValue.min(SystemBorrowableValue);
    }

    public static String getDuputyKavaAddress(String denom) {
        if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_BNB)) {
            return KAVA_MAIN_BNB_DEPUTY;
        } else if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_BTCB)) {
            return KAVA_MAIN_BTCB_DEPUTY;
        } else if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_XRPB)) {
            return KAVA_MAIN_XRPB_DEPUTY;
        } else if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_BUSD)) {
            return KAVA_MAIN_BUSD_DEPUTY;
        }
        return "";
    }

    public static String getDuputyBnbAddress(String denom) {
        if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_BNB)) {
            return BINANCE_MAIN_BNB_DEPUTY;
        } else if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_BTCB)) {
            return BINANCE_MAIN_BTCB_DEPUTY;
        } else if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_XRPB)) {
            return BINANCE_MAIN_XRPB_DEPUTY;
        } else if (denom.equalsIgnoreCase(TOKEN_HTLC_KAVA_BUSD)) {
            return BINANCE_MAIN_BUSD_DEPUTY;
        }
        return "";
    }

    public static BigDecimal getBnbTokenUserCurrencyPrice(BaseData baseData, Currency currency, String denom, PriceProvider priceProvider) {
        BigDecimal result = BigDecimal.ZERO;
        for (BnbTicker ticker : baseData.mBnbTickers) {
            if (ticker.symbol.equals(getBnbTicSymbol(denom))) {
                BigDecimal perPrice;
                if (isBnbBaseMarketToken(denom)) {
                    perPrice = BigDecimal.ONE.divide(new BigDecimal(ticker.lastPrice), 8, RoundingMode.DOWN);
                } else {
                    perPrice = BigDecimal.ONE.multiply(new BigDecimal(ticker.lastPrice)).setScale(8, RoundingMode.DOWN);
                }
                return perPrice.multiply(WDp.perUserCurrencyValue(baseData, currency, BNB_MAIN.INSTANCE.getMainDenom(), priceProvider));
            }
        }
        return result;
    }

    public static SpannableString dpBnbTokenUserCurrencyPrice(BaseData baseData, Currency currency, String denom, PriceProvider priceProvider) {
        final String formatted = currency.getSymbol() + " " + WDp.getDecimalFormat(3).format(getBnbTokenUserCurrencyPrice(baseData, currency, denom, priceProvider));
        return WDp.getDpString(formatted, 3);
    }

    public static BigDecimal getBnbConvertAmount(BaseData baseData, String denom, BigDecimal amount) {
        BigDecimal result = BigDecimal.ZERO;
//        Log.d("fftf", "getBnbConvertAmount: " + denom + " " + amount);
        for (BnbTicker ticker : baseData.mBnbTickers) {
//            Log.d("fftf", "getBnbConvertAmount ticker.symbol: " + ticker.symbol + " getBnbTicSymbol(denom): " + getBnbTicSymbol(denom));
            if (ticker.symbol.equals(getBnbTicSymbol(denom))) {
//                Log.d("fftf", "getBnbConvertAmount isBnbBaseMarketToken(denom): " + isBnbBaseMarketToken(denom));
                if (isBnbBaseMarketToken(denom)) {
                    return amount.divide(new BigDecimal(ticker.lastPrice), 8, RoundingMode.DOWN);
                } else {
                    return amount.multiply(new BigDecimal(ticker.lastPrice)).setScale(8, RoundingMode.DOWN);
                }
            }
        }
        return result;
    }

    public static boolean isBnbBaseMarketToken(String symbol) {
        switch (symbol) {
            case "USDT.B-B7C":
            case "ETH.B-261":
            case "BTC.B-918":
            case "USDSB-1AC":
            case "THKDB-888":
            case "TUSDB-888":
            case "BTCB-1DE":
            case "ETH-1C9":
            case "IDRTB-178":
            case "BUSD-BD1":
            case "TAUDB-888":
                return true;
        }
        return false;
    }

    public static String getBnbTicSymbol(String symbol) {
        if (isBnbBaseMarketToken(symbol)) {
            return BNB_MAIN.INSTANCE.getMainDenom() + "_" + symbol;

        } else {
            return symbol + "_" + BNB_MAIN.INSTANCE.getMainDenom();
        }
    }

    public static String getMonikerName(String opAddress, List<Validator> validators, boolean bracket) {
        String result = "";
        for (Validator val : validators) {
            if (val.operator_address.equals(opAddress)) {
                if (bracket) {
                    return "(" + val.description.moniker + ")";
                } else {
                    return val.description.moniker;
                }

            }
        }
        return result;
    }


    public static byte[] long2Bytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(x);
        return buffer.array();
    }

    public static boolean isValidStarName(String starname) {
        String[] names = starname.split("\\*");
        if (names.length != 2) {
            return false;
        }
        return isValidAccount(names[0]) && isValidDomain(names[1]);
    }

    public static boolean isValidDomain(String starname) {
        return Pattern
                .compile("^[mabcdefghijklnopqrstuvwxy][-a-z0-9_]{0,2}$|^[-a-z0-9_]{4,32}$")
                .matcher(starname)
                .matches();
    }

    public static boolean isValidAccount(String starname) {
        return Pattern
                .compile("^[-.a-z0-9_]{1,63}$")
                .matcher(starname)
                .matches();
    }

    public static String checkStarnameWithResource(BaseChain chain, List<Types.Resource> resources) {
        for (Types.Resource resource : resources) {
            if (WDp.isValidChainAddress(chain, resource.getResource())) {
                return resource.getResource();
            }
        }
        return "";

    }

    public static BigDecimal getRealBlockPerYear(BaseChain chain) {
        BigDecimal result = BigDecimal.ZERO;
        if (chain != null) {
            BigDecimal blockTime = chain.getBlockTime();
            if (!blockTime.equals(BigDecimal.ZERO)) {
                result = YEAR_SEC.divide(blockTime, 2, RoundingMode.DOWN);
            }
        }

        return result;
    }

    /**
     * coingeko
     */
    public static Intent getCoingekoIntent(MainActivity mainActivity) {
        if (mainActivity.getBaseChain().equals(COSMOS_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/cosmos")));

        } else if (mainActivity.getBaseChain().equals(IMVERSED_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/imversed")));

        } else if (mainActivity.getBaseChain().equals(IRIS_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/irisnet")));

        } else if (mainActivity.getBaseChain().equals(IOV_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/starname")));

        } else if (mainActivity.getBaseChain().equals(BNB_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/binancecoin")));

        } else if (mainActivity.getBaseChain().equals(KAVA_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/kava")));

        } else if (mainActivity.getBaseChain().equals(BAND_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/band-protocol")));

        } else if (mainActivity.getBaseChain().equals(CERTIK_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/certik")));

        } else if (mainActivity.getBaseChain().equals(SECRET_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/secret")));

        } else if (mainActivity.getBaseChain().equals(AKASH_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/akash-network")));

        } else if (mainActivity.getBaseChain().equals(OKEX_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/okexchain")));

        } else if (mainActivity.getBaseChain().equals(SENTINEL_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/sentinel")));

        } else if (mainActivity.getBaseChain().equals(PERSIS_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/persistence")));

        } else if (mainActivity.getBaseChain().equals(FETCHAI_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/fetch-ai")));

        } else if (mainActivity.getBaseChain().equals(CRYPTO_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/crypto-com-chain")));

        } else if (mainActivity.getBaseChain().equals(SIF_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/sifchain")));

        } else if (mainActivity.getBaseChain().equals(KI_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/ki")));

        } else if (mainActivity.getBaseChain().equals(OSMOSIS_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/osmosis")));

        } else if (mainActivity.getBaseChain().equals(MEDI_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/medibloc")));

        } else if (mainActivity.getBaseChain().equals(EMONEY_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/e-money")));

        } else if (mainActivity.getBaseChain().equals(JUNO_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/juno-network")));

        } else if (mainActivity.getBaseChain().equals(REGEN_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/regen")));

        } else if (mainActivity.getBaseChain().equals(BITCANNA_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/bitcanna")));

        } else if (mainActivity.getBaseChain().equals(INJ_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/injective-protocol")));

        } else if (mainActivity.getBaseChain().equals(BITSONG_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/bitsong")));

        } else if (mainActivity.getBaseChain().equals(RIZON_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/rizon")));

        } else if (mainActivity.getBaseChain().equals(COMDEX_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/comdex")));

        } else if (mainActivity.getBaseChain().equals(STARGAZE_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/stargaze")));

        } else if (mainActivity.getBaseChain().equals(LUM_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/lum-network")));

        } else if (mainActivity.getBaseChain().equals(DESMOS_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/desmos")));

        } else if (mainActivity.getBaseChain().equals(CHIHUAHUA_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/chihuahua-chain")));

        } else if (mainActivity.getBaseChain().equals(UMEE_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/umee")));

        } else if (mainActivity.getBaseChain().equals(CUDOS_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/cudos")));

        } else if (mainActivity.getBaseChain().equals(GRABRIDGE_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/graviton")));

//        } else if (mainActivity.getBaseChain().equals(ASSETMANTLE_MAIN.INSTANCE)) {
//            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/assetmantle")));

        } else if (mainActivity.getBaseChain().equals(CERBERUS_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/cerberus")));

        } else if (mainActivity.getBaseChain().equals(EVMOS_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/evmos")));

        } else if (mainActivity.getBaseChain().equals(KONSTELL_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/konstellation")));

        } else if (mainActivity.getBaseChain().equals(PROVENANCE_MAIN.INSTANCE)) {
            mainActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coingecko.com/en/coins/provenance-blockchain")));
        }
        return null;
    }

    /**
     * Main Guide
     */
    public static void getGuide(MainActivity mainActivity, ImageView guideImg, TextView guideTitle, TextView guideMsg, Button guideBtn1, Button guideBtn2) {
        guideBtn1.setText(R.string.str_home);
        guideBtn2.setText(R.string.str_blog);
        if (mainActivity.getBaseChain().equals(COSMOS_MAIN.INSTANCE) || mainActivity.getBaseChain().equals(COSMOS_TEST.INSTANCE)) {
            guideImg.setImageResource(R.drawable.guide_img);
            guideTitle.setText(R.string.str_front_guide_title);
            guideMsg.setText(R.string.str_front_guide_msg);
            guideBtn1.setText(R.string.str_guide);
            guideBtn2.setText(R.string.str_faq);

        } else if (mainActivity.getBaseChain().equals(IMVERSED_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_imversed);
            guideTitle.setText(R.string.str_front_guide_title_imversed);
            guideMsg.setText(R.string.str_front_guide_msg_imversed);

        } else if (mainActivity.getBaseChain().equals(IRIS_MAIN.INSTANCE) || mainActivity.getBaseChain().equals(IRIS_TEST.INSTANCE)) {
            guideImg.setImageResource(R.drawable.irisnet_img);
            guideTitle.setText(R.string.str_front_guide_title_iris);
            guideMsg.setText(R.string.str_front_guide_msg_iris);

        } else if (mainActivity.getBaseChain().equals(BNB_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.binance_img);
            guideTitle.setText(R.string.str_front_guide_title_binance);
            guideMsg.setText(R.string.str_front_guide_msg_bnb);
            guideBtn1.setText(R.string.str_faq_bnb);
            guideBtn2.setText(R.string.str_guide_bnb);

        } else if (mainActivity.getBaseChain().equals(KAVA_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.kavamain_img);
            guideTitle.setText(R.string.str_front_guide_title_kava);
            guideMsg.setText(R.string.str_front_guide_msg_kava);

        } else if (mainActivity.getBaseChain().equals(IOV_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.iov_img);
            guideTitle.setText(R.string.str_front_guide_title_iov);
            guideMsg.setText(R.string.str_front_guide_msg_iov);

        } else if (mainActivity.getBaseChain().equals(BAND_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_bandprotocol);
            guideTitle.setText(R.string.str_front_guide_title_band);
            guideMsg.setText(R.string.str_front_guide_msg_band);

        } else if (mainActivity.getBaseChain().equals(OKEX_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_okx);
            guideTitle.setText(R.string.str_front_guide_title_ok);
            guideMsg.setText(R.string.str_front_guide_msg_ok);
            guideBtn1.setText(R.string.str_faq_ok);
            guideBtn2.setText(R.string.str_guide_ok);

        } else if (mainActivity.getBaseChain().equals(CERTIK_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.certik_img);
            guideTitle.setText(R.string.str_front_guide_title_certik);
            guideMsg.setText(R.string.str_front_guide_msg_certik);

        } else if (mainActivity.getBaseChain().equals(AKASH_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.akash_img);
            guideTitle.setText(R.string.str_front_guide_title_akash);
            guideMsg.setText(R.string.str_front_guide_msg_akash);

        } else if (mainActivity.getBaseChain().equals(SECRET_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.secret_img);
            guideTitle.setText(R.string.str_front_guide_title_secret);
            guideMsg.setText(R.string.str_front_guide_msg_secret);

        } else if (mainActivity.getBaseChain().equals(PERSIS_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.persistence_img);
            guideTitle.setText(R.string.str_front_guide_title_persis);
            guideMsg.setText(R.string.str_front_guide_msg_persis);

        } else if (mainActivity.getBaseChain().equals(SENTINEL_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.sentinel_img);
            guideTitle.setText(R.string.str_front_guide_title_sentinel);
            guideMsg.setText(R.string.str_front_guide_msg_sentinel);

        } else if (mainActivity.getBaseChain().equals(FETCHAI_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.fetchai_img);
            guideTitle.setText(R.string.str_front_guide_title_fetch);
            guideMsg.setText(R.string.str_front_guide_msg_fetch);

        } else if (mainActivity.getBaseChain().equals(CRYPTO_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.cryptochain_img);
            guideTitle.setText(R.string.str_front_guide_title_crypto);
            guideMsg.setText(R.string.str_front_guide_msg_crypto);

        } else if (mainActivity.getBaseChain().equals(SIF_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.sifchain_img);
            guideTitle.setText(R.string.str_front_guide_title_sif);
            guideMsg.setText(R.string.str_front_guide_msg_sif);

        } else if (mainActivity.getBaseChain().equals(KI_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.kifoundation_img);
            guideTitle.setText(R.string.str_front_guide_title_ki);
            guideMsg.setText(R.string.str_front_guide_msg_ki);

        } else if (mainActivity.getBaseChain().equals(OSMOSIS_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_osmosis);
            guideTitle.setText(R.string.str_front_guide_title_osmosis);
            guideMsg.setText(R.string.str_front_guide_msg_osmosis);

        } else if (mainActivity.getBaseChain().equals(RIZON_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_rizon);
            guideTitle.setText(R.string.str_front_guide_title_rizon);
            guideMsg.setText(R.string.str_front_guide_msg_rizon);

        } else if (mainActivity.getBaseChain().equals(MEDI_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.medibloc_img);
            guideTitle.setText(R.string.str_front_guide_title_medi);
            guideMsg.setText(R.string.str_front_guide_msg_medi);

        } else if (mainActivity.getBaseChain().equals(EMONEY_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_emoney);
            guideTitle.setText(R.string.str_front_guide_title_emoney);
            guideMsg.setText(R.string.str_front_guide_msg_emoney);

        } else if (mainActivity.getBaseChain().equals(JUNO_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_juno);
            guideTitle.setText(R.string.str_front_guide_title_juno);
            guideMsg.setText(R.string.str_front_guide_msg_juno);

        } else if (mainActivity.getBaseChain().equals(REGEN_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_regen);
            guideTitle.setText(R.string.str_front_guide_title_regen);
            guideMsg.setText(R.string.str_front_guide_msg_regen);

        } else if (mainActivity.getBaseChain().equals(BITCANNA_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_bitcanna);
            guideTitle.setText(R.string.str_front_guide_title_bitcanna);
            guideMsg.setText(R.string.str_front_guide_msg_bitcanna);

        } else if (mainActivity.getBaseChain().equals(ALTHEA_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_althea);
            guideTitle.setText(R.string.str_front_guide_title_althea);
            guideMsg.setText(R.string.str_front_guide_msg_althea);

        } else if (mainActivity.getBaseChain().equals(STARGAZE_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_stargaze);
            guideTitle.setText(R.string.str_front_guide_title_stargaze);
            guideMsg.setText(R.string.str_front_guide_msg_stargaze);

        } else if (mainActivity.getBaseChain().equals(GRABRIDGE_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_gravitybridge);
            guideTitle.setText(R.string.str_front_guide_title_grabridge);
            guideMsg.setText(R.string.str_front_guide_msg_grabridge);

        } else if (mainActivity.getBaseChain().equals(COMDEX_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_comdex);
            guideTitle.setText(R.string.str_front_guide_title_comdex);
            guideMsg.setText(R.string.str_front_guide_msg_comdex);

        } else if (mainActivity.getBaseChain().equals(INJ_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_injective);
            guideTitle.setText(R.string.str_front_guide_title_inj);
            guideMsg.setText(R.string.str_front_guide_msg_inj);

        } else if (mainActivity.getBaseChain().equals(BITSONG_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_bitsong);
            guideTitle.setText(R.string.str_front_guide_title_bitsong);
            guideMsg.setText(R.string.str_front_guide_msg_bitsong);

        } else if (mainActivity.getBaseChain().equals(DESMOS_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_desmos);
            guideTitle.setText(R.string.str_front_guide_title_desmos);
            guideMsg.setText(R.string.str_front_guide_msg_desmos);

        } else if (mainActivity.getBaseChain().equals(LUM_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_lumnetwork);
            guideTitle.setText(R.string.str_front_guide_title_lum);
            guideMsg.setText(R.string.str_front_guide_msg_lum);

        } else if (mainActivity.getBaseChain().equals(CHIHUAHUA_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_chihuahua);
            guideTitle.setText(R.string.str_front_guide_title_chihuahua);
            guideMsg.setText(R.string.str_front_guide_msg_chihuahua);

        } else if (mainActivity.getBaseChain().equals(UMEE_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_umee);
            guideTitle.setText(R.string.str_front_guide_title_umee);
            guideMsg.setText(R.string.str_front_guide_msg_umee);

        } else if (mainActivity.getBaseChain().equals(AXELAR_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_axelar);
            guideTitle.setText(R.string.str_front_guide_title_axelar);
            guideMsg.setText(R.string.str_front_guide_msg_axelar);

        } else if (mainActivity.getBaseChain().equals(KONSTELL_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_konstellation);
            guideTitle.setText(R.string.str_front_guide_title_konstellation);
            guideMsg.setText(R.string.str_front_guide_msg_konstellation);

        } else if (mainActivity.getBaseChain().equals(EVMOS_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_evmos);
            guideTitle.setText(R.string.str_front_guide_title_evmos);
            guideMsg.setText(R.string.str_front_guide_msg_evmos);

        } else if (mainActivity.getBaseChain().equals(CUDOS_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_cudos);
            guideTitle.setText(R.string.str_front_guide_title_cudos);
            guideMsg.setText(R.string.str_front_guide_msg_cudos);

        } else if (mainActivity.getBaseChain().equals(PROVENANCE_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_provenance);
            guideTitle.setText(R.string.str_front_guide_title_provenance);
            guideMsg.setText(R.string.str_front_guide_msg_provenance);

        } else if (mainActivity.getBaseChain().equals(CERBERUS_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_cerberus);
            guideTitle.setText(R.string.str_front_guide_title_cerberus);
            guideMsg.setText(R.string.str_front_guide_msg_cerberus);

        } else if (mainActivity.getBaseChain().equals(OMNIFLIX_MAIN.INSTANCE)) {
            guideImg.setImageResource(R.drawable.infoicon_omniflix);
            guideTitle.setText(R.string.str_front_guide_title_omniflix);
            guideMsg.setText(R.string.str_front_guide_msg_omniflix);

        }
    }

    public static void getWalletData(BaseChain chain, ImageView coinImg, TextView coinDenom) {
        coinImg.setImageResource(chain.getCoinIcon());
        coinDenom.setText(chain.getSymbolTitle());
        coinDenom.setTextColor(ContextCompat.getColor(coinDenom.getContext(), chain.getChainColor()));
    }

    public static void getDexTitle(BaseChain chain, RelativeLayout mBtnDex, TextView dexTitle) {
        if (chain.equals(COSMOS_MAIN.INSTANCE)) {
            mBtnDex.setVisibility(View.VISIBLE);
            dexTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_gravitydex, 0, 0, 0);
            dexTitle.setText(R.string.str_gravity_dex);
        } else if (chain.equals(IRIS_MAIN.INSTANCE) || chain.equals(CRYPTO_MAIN.INSTANCE)) {
            mBtnDex.setVisibility(View.VISIBLE);
            dexTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_nft, 0, 0, 0);
            dexTitle.setText(R.string.str_nft_c);
        } else if (chain.equals(IOV_MAIN.INSTANCE)) {
            mBtnDex.setVisibility(View.VISIBLE);
            dexTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.name_ic, 0, 0, 0);
            dexTitle.setText(R.string.str_starname_service);
        } else if (chain.equals(KAVA_MAIN.INSTANCE)) {
            mBtnDex.setVisibility(View.VISIBLE);
            dexTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cdp_s_ic, 0, 0, 0);
            dexTitle.setText(R.string.str_kava_dapp);
        } else if (chain.equals(SIF_MAIN.INSTANCE)) {
            mBtnDex.setVisibility(View.VISIBLE);
            dexTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_sifdex, 0, 0, 0);
            dexTitle.setText(R.string.str_sif_dex_title);
        } else if (chain.equals(OSMOSIS_MAIN.INSTANCE)) {
            mBtnDex.setVisibility(View.VISIBLE);
            dexTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_osmosislab, 0, 0, 0);
            dexTitle.setText(R.string.str_osmosis_defi_lab);
        } else if (chain.equals(DESMOS_MAIN.INSTANCE)) {
            mBtnDex.setVisibility(View.VISIBLE);
            dexTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_profile, 0, 0, 0);
            dexTitle.setText(R.string.str_desmos_airdrop);
        } else {
            mBtnDex.setVisibility(View.GONE);
        }
    }

    public static Intent getGuide1Intent(BaseChain chain) {
        if (chain.equals(COSMOS_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://cosmos.network/"));

        } else if (chain.equals(IMVERSED_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://imversed.com"));

        } else if (chain.equals(IRIS_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.irisnet.org/"));

        } else if (chain.equals(BNB_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.binance.org"));

        } else if (chain.equals(KAVA_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.kava.io/registration/"));

        } else if (chain.equals(IOV_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.starname.me/"));

        } else if (chain.equals(BAND_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://bandprotocol.com/"));

        } else if (chain.equals(OKEX_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.okex.com/"));

        } else if (chain.equals(CERTIK_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.certik.foundation/"));

        } else if (chain.equals(AKASH_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://akash.network/"));

        } else if (chain.equals(SECRET_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://scrt.network"));

        } else if (chain.equals(PERSIS_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://persistence.one/"));

        } else if (chain.equals(SENTINEL_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://sentinel.co/"));

        } else if (chain.equals(FETCHAI_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://fetch.ai/"));

        } else if (chain.equals(CRYPTO_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://crypto.org/"));

        } else if (chain.equals(SIF_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://sifchain.finance/"));

        } else if (chain.equals(KI_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://foundation.ki/en"));

        } else if (chain.equals(OSMOSIS_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://osmosis.zone/"));

        } else if (chain.equals(MEDI_MAIN.INSTANCE)) {
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("ko")) {
                return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medibloc.com"));
            } else {
                return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medibloc.com/en/ "));
            }

        } else if (chain.equals(EMONEY_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.e-money.com/"));

        } else if (chain.equals(RIZON_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hdactech.com/en/index.do"));

        } else if (chain.equals(JUNO_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://junochain.com/"));

        } else if (chain.equals(REGEN_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.regen.network/"));

        } else if (chain.equals(BITCANNA_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bitcanna.io/"));

        } else if (chain.equals(ALTHEA_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.althea.net/"));

        } else if (chain.equals(STARGAZE_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://stargaze.zone/"));

        } else if (chain.equals(GRABRIDGE_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gravitybridge.net/"));

        } else if (chain.equals(COMDEX_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://comdex.one/home"));

        } else if (chain.equals(INJ_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://injectiveprotocol.com/"));

        } else if (chain.equals(BITSONG_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://explorebitsong.com/"));

        } else if (chain.equals(DESMOS_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.desmos.network/"));

        } else if (chain.equals(LUM_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://lum.network/"));

        } else if (chain.equals(CHIHUAHUA_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://chi.huahua.wtf/"));

        } else if (chain.equals(UMEE_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://umee.cc/"));

        } else if (chain.equals(AXELAR_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://axelar.network/"));

        } else if (chain.equals(KONSTELL_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://konstellation.tech/"));

        } else if (chain.equals(EVMOS_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://evmos.org/"));

        } else if (chain.equals(CUDOS_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cudos.org/"));

        } else if (chain.equals(PROVENANCE_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.provenance.io/"));

        } else if (chain.equals(CERBERUS_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://cerberus.zone/"));

        } else if (chain.equals(OMNIFLIX_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://omniflix.network/"));
        }
        return null;
    }

    public static Intent getGuide2Intent(BaseChain chain) {
        if (chain.equals(COSMOS_MAIN.INSTANCE)) {
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("ko")) {
                return new Intent(Intent.ACTION_VIEW, Uri.parse("https://guide.cosmostation.io/app_wallet_ko.html"));
            } else {
                return new Intent(Intent.ACTION_VIEW, Uri.parse("https://guide.cosmostation.io/app_wallet_en.html"));
            }

        } else if (chain.equals(IMVERSED_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://imversed.com"));     // TODO Imversed

        } else if (chain.equals(IRIS_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/irisnet-blog"));

        } else if (chain.equals(BNB_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/@binance"));

        } else if (chain.equals(KAVA_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/kava-labs"));

        } else if (chain.equals(IOV_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/iov-internet-of-values"));

        } else if (chain.equals(BAND_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/bandprotocol"));

        } else if (chain.equals(OKEX_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.okex.com/community"));

        } else if (chain.equals(CERTIK_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.certik.foundation/blog"));

        } else if (chain.equals(AKASH_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://akash.network/"));

        } else if (chain.equals(SECRET_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.scrt.network"));

        } else if (chain.equals(PERSIS_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/persistence-blog"));

        } else if (chain.equals(SENTINEL_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/sentinel"));

        } else if (chain.equals(FETCHAI_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://fetch.ai/blog/"));

        } else if (chain.equals(CRYPTO_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://crypto.org/community/"));

        } else if (chain.equals(SIF_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/sifchain-finance"));

        } else if (chain.equals(KI_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/ki-foundation"));

        } else if (chain.equals(OSMOSIS_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/osmosis"));

        } else if (chain.equals(MEDI_MAIN.INSTANCE)) {
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("ko")) {
                return new Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.medibloc.org/"));
            } else {
                return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/medibloc"));
            }

        } else if (chain.equals(EMONEY_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://emoneytokenstandard.org/"));

        } else if (chain.equals(RIZON_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/hdac"));

        } else if (chain.equals(JUNO_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/@JunoNetwork"));

        } else if (chain.equals(REGEN_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/regen-network"));

        } else if (chain.equals(BITCANNA_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/@BitCannaGlobal"));

        } else if (chain.equals(ALTHEA_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.althea.net/"));

        } else if (chain.equals(STARGAZE_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/stargaze-protocol"));

        } else if (chain.equals(GRABRIDGE_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gravitybridge.net/blog"));

        } else if (chain.equals(COMDEX_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.comdex.one"));

        } else if (chain.equals(INJ_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.injectiveprotocol.com/"));

        } else if (chain.equals(BITSONG_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://bitsongofficial.medium.com/"));

        } else if (chain.equals(DESMOS_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/desmosnetwork"));

        } else if (chain.equals(LUM_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/lum-network"));

        } else if (chain.equals(CHIHUAHUA_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://chi.huahua.wtf"));

        } else if (chain.equals(UMEE_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/umeeblog"));

        } else if (chain.equals(AXELAR_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://axelar.network/blog"));

        } else if (chain.equals(KONSTELL_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://konstellation.medium.com/"));

        } else if (chain.equals(EVMOS_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://evmos.blog/"));

        } else if (chain.equals(CUDOS_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cudos.org/blog/"));

        } else if (chain.equals(PROVENANCE_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.provenance.io/blog/"));

        } else if (chain.equals(CERBERUS_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://medium.com/@cerberus_zone"));

        } else if (chain.equals(OMNIFLIX_MAIN.INSTANCE)) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.omniflix.network/"));

        }
        return null;
    }

    public static String getExplorer(BaseChain basechain) {
        if (basechain.equals(BNB_MAIN.INSTANCE)) {
            return EXPLORER_BINANCE_MAIN;

        } else if (basechain.equals(OKEX_MAIN.INSTANCE)) {
            return EXPLORER_OKEX_MAIN;

        } else if (basechain.equals(SECRET_MAIN.INSTANCE)) {
            return EXPLORER_SECRET_MAIN;

        } else if (basechain.equals(COSMOS_MAIN.INSTANCE)) {
            return EXPLORER_COSMOS_MAIN;

        } else if (basechain.equals(IMVERSED_MAIN.INSTANCE)) {
            return EXPLORER_IMVERSED_MAIN;

        } else if (basechain.equals(IRIS_MAIN.INSTANCE)) {
            return EXPLORER_IRIS_MAIN;

        } else if (basechain.equals(AKASH_MAIN.INSTANCE)) {
            return EXPLORER_AKASH_MAIN;

        } else if (basechain.equals(SENTINEL_MAIN.INSTANCE)) {
            return EXPLORER_SENTINEL_MAIN;

        } else if (basechain.equals(PERSIS_MAIN.INSTANCE)) {
            return EXPLORER_PERSIS_MAIN;

        } else if (basechain.equals(CRYPTO_MAIN.INSTANCE)) {
            return EXPLORER_CRYPTOORG_MAIN;

        } else if (basechain.equals(OSMOSIS_MAIN.INSTANCE)) {
            return EXPLORER_OSMOSIS_MAIN;

        } else if (basechain.equals(IOV_MAIN.INSTANCE)) {
            return EXPLORER_IOV_MAIN;

        } else if (basechain.equals(SIF_MAIN.INSTANCE)) {
            return EXPLORER_SIF_MAIN;

        } else if (basechain.equals(BAND_MAIN.INSTANCE)) {
            return EXPLORER_BAND_MAIN;

        } else if (basechain.equals(MEDI_MAIN.INSTANCE)) {
            return EXPLORER_MEDI_MAIN;

        } else if (basechain.equals(CERTIK_MAIN.INSTANCE)) {
            return EXPLORER_CERTIK_MAIN;

        } else if (basechain.equals(EMONEY_MAIN.INSTANCE)) {
            return EXPLORER_EMONEY_MAIN;

        } else if (basechain.equals(FETCHAI_MAIN.INSTANCE)) {
            return EXPLORER_FETCHAI_MAIN;

        } else if (basechain.equals(RIZON_MAIN.INSTANCE)) {
            return EXPLORER_RIZON_MAIN;

        } else if (basechain.equals(JUNO_MAIN.INSTANCE)) {
            return EXPLORER_JUNO_MAIN;

        } else if (basechain.equals(REGEN_MAIN.INSTANCE)) {
            return EXPLORER_REGEN_MAIN;

        } else if (basechain.equals(BITCANNA_MAIN.INSTANCE)) {
            return EXPLORER_BITCANNA_MAIN;

        } else if (basechain.equals(ALTHEA_MAIN.INSTANCE)) {
            return EXPLORER_ALTHEA_MAIN;

        } else if (basechain.equals(STARGAZE_MAIN.INSTANCE)) {
            return EXPLORER_STARGAZE_MAIN;

        } else if (basechain.equals(GRABRIDGE_MAIN.INSTANCE)) {
            return EXPLORER_GRABRIDGE_MAIN;

        } else if (basechain.equals(KI_MAIN.INSTANCE)) {
            return EXPLORER_KI_MAIN;

        } else if (basechain.equals(COMDEX_MAIN.INSTANCE)) {
            return EXPLORER_COMDEX_MAIN;

        } else if (basechain.equals(INJ_MAIN.INSTANCE)) {
            return EXPLORER_INJ_MAIN;

        } else if (basechain.equals(BITSONG_MAIN.INSTANCE)) {
            return EXPLORER_BITSONG_MAIN;

        } else if (basechain.equals(DESMOS_MAIN.INSTANCE)) {
            return EXPLORER_DESMOS_MAIN;

        } else if (basechain.equals(LUM_MAIN.INSTANCE)) {
            return EXPLORER_LUM_MAIN;

        } else if (basechain.equals(CHIHUAHUA_MAIN.INSTANCE)) {
            return EXPLORER_CHIHUAHUA_MAIN;

        } else if (basechain.equals(KAVA_MAIN.INSTANCE)) {
            return EXPLORER_KAVA_MAIN;

        } else if (basechain.equals(AXELAR_MAIN.INSTANCE)) {
            return EXPLORER_AXELAR_MAIN;

        } else if (basechain.equals(KONSTELL_MAIN.INSTANCE)) {
            return EXPLORER_KONSTELL_MAIN;

        } else if (basechain.equals(UMEE_MAIN.INSTANCE)) {
            return EXPLORER_UMEE_MAIN;

        } else if (basechain.equals(EVMOS_MAIN.INSTANCE)) {
            return EXPLORER_EVMOS_MAIN;

        } else if (basechain.equals(CUDOS_MAIN.INSTANCE)) {
            return EXPLORER_CUDOS_MAIN;

        } else if (basechain.equals(PROVENANCE_MAIN.INSTANCE)) {
            return EXPLORER_PROVENANCE_MAIN;

        } else if (basechain.equals(CERBERUS_MAIN.INSTANCE)) {
            return EXPLORER_CERBERUS_MAIN;

        } else if (basechain.equals(OMNIFLIX_MAIN.INSTANCE)) {
            return EXPLORER_OMNIFLIX_MAIN;

        } else if (basechain.equals(COSMOS_TEST.INSTANCE)) {
            return EXPLORER_COSMOS_TEST;

        } else if (basechain.equals(IRIS_TEST.INSTANCE)) {
            return EXPLORER_IRIS_TEST;

        }
        return "";
    }

    public static String getExplorerSuffix(BaseChain basechain) {
        if (basechain.equals(OKEX_MAIN.INSTANCE)) return "tx/0x";
        else if (basechain.equals(IMVERSED_MAIN.INSTANCE)) return "transactions/";
        else return "txs/";
    }

    public static String getTxExplorer(BaseChain basechain, String hash) {
        String result = "";
        if (hash != null) {
            String explorer = getExplorer(basechain);
            String suffix = getExplorerSuffix(basechain);

            if (!TextUtils.isEmpty(explorer)) {
                result = explorer + suffix + hash;
            }
        }
        return result;
    }

    /**
     * Chain Gas Amount
     */
    public static BigDecimal getEstimateGasAmount(Context c, BaseChain basechain, int txType, int valCnt) {
        BigDecimal result = BigDecimal.ZERO;
        if (basechain.equals(IOV_MAIN.INSTANCE)) {
            if (txType == CONST_PW_TX_SIMPLE_SEND) {
                return new BigDecimal(IOV_GAS_AMOUNT_SEND);
            } else if (txType == CONST_PW_TX_SIMPLE_DELEGATE) {
                return new BigDecimal(IOV_GAS_AMOUNT_STAKE);
            } else if (txType == CONST_PW_TX_SIMPLE_UNDELEGATE) {
                return new BigDecimal(IOV_GAS_AMOUNT_STAKE);
            } else if (txType == CONST_PW_TX_SIMPLE_REDELEGATE) {
                return new BigDecimal(IOV_GAS_AMOUNT_REDELEGATE);
            } else if (txType == CONST_PW_TX_REINVEST) {
                return new BigDecimal(IOV_GAS_AMOUNT_REINVEST);
            } else if (txType == CONST_PW_TX_SIMPLE_REWARD) {
                ArrayList<String> rewardGasFees = new ArrayList<String>(Arrays.asList(c.getResources().getStringArray(R.array.gas_multi_reward_kava)));
                return new BigDecimal(rewardGasFees.get(valCnt - 1));
            } else if (txType == CONST_PW_TX_SIMPLE_CHANGE_REWARD_ADDRESS) {
                return new BigDecimal(IOV_GAS_AMOUNT_LOW);
            } else if (txType == CONST_PW_TX_VOTE) {
                return new BigDecimal(IOV_GAS_AMOUNT_LOW);
            } else if (txType == CONST_PW_TX_REGISTER_DOMAIN || txType == CONST_PW_TX_REGISTER_ACCOUNT) {
                return new BigDecimal(IOV_GAS_AMOUNT_REGISTER);
            } else if (txType == CONST_PW_TX_DELETE_DOMAIN || txType == CONST_PW_TX_DELETE_ACCOUNT) {
                return new BigDecimal(IOV_GAS_AMOUNT_DELETE);
            } else if (txType == CONST_PW_TX_RENEW_DOMAIN || txType == CONST_PW_TX_RENEW_ACCOUNT) {
                return new BigDecimal(IOV_GAS_AMOUNT_RENEW);
            } else if (txType == CONST_PW_TX_REPLACE_STARNAME) {
                return new BigDecimal(IOV_GAS_AMOUNT_REPLACE);
            } else if (txType == CONST_PW_TX_IBC_TRANSFER) {
                return new BigDecimal(IOV_GAS_AMOUNT_IBC_SEND);
            }

        } else if (basechain.equals(OSMOSIS_MAIN.INSTANCE)) {
            if (txType == CONST_PW_TX_SIMPLE_SEND) {
                return new BigDecimal(OSMOSIS_GAS_AMOUNT_SEND);
            } else if (txType == CONST_PW_TX_SIMPLE_DELEGATE) {
                return new BigDecimal(OSMOSIS_GAS_AMOUNT_STAKE);
            } else if (txType == CONST_PW_TX_SIMPLE_UNDELEGATE) {
                return new BigDecimal(OSMOSIS_GAS_AMOUNT_STAKE);
            } else if (txType == CONST_PW_TX_SIMPLE_REDELEGATE) {
                return new BigDecimal(OSMOSIS_GAS_AMOUNT_REDELEGATE);
            } else if (txType == CONST_PW_TX_REINVEST) {
                return new BigDecimal(OSMOSIS_GAS_AMOUNT_REINVEST);
            } else if (txType == CONST_PW_TX_SIMPLE_REWARD) {
                ArrayList<String> rewardGasFees = new ArrayList<String>(Arrays.asList(c.getResources().getStringArray(R.array.gas_multi_reward)));
                return new BigDecimal(rewardGasFees.get(valCnt - 1));
            } else if (txType == CONST_PW_TX_SIMPLE_CHANGE_REWARD_ADDRESS) {
                return new BigDecimal(OSMOSIS_GAS_AMOUNT_LOW);
            } else if (txType == CONST_PW_TX_VOTE) {
                return new BigDecimal(OSMOSIS_GAS_AMOUNT_LOW);
            } else if (txType == CONST_PW_TX_OSMOSIS_SWAP) {
                return new BigDecimal(OSMOSIS_GAS_AMOUNT_SWAP);
            } else if (txType == CONST_PW_TX_OSMOSIS_JOIN_POOL || txType == CONST_PW_TX_OSMOSIS_EXIT_POOL) {
                return new BigDecimal(OSMOSIS_GAS_AMOUNT_POOL);
            } else if (txType == CONST_PW_TX_OSMOSIS_EARNING) {
                return new BigDecimal(OSMOSIS_GAS_AMOUNT_LOCK);
            } else if (txType == CONST_PW_TX_OSMOSIS_BEGIN_UNBONDING) {
                return new BigDecimal(OSMOSIS_GAS_AMOUNT_BEGIN_UNBONDING);
            } else if (txType == CONST_PW_TX_OSMOSIS_UNLOCK) {
                return new BigDecimal(OSMOSIS_GAS_AMOUNT_UNLOCK);
            } else if (txType == CONST_PW_TX_IBC_TRANSFER) {
                return new BigDecimal(OSMOSIS_GAS_AMOUNT_IBC_SEND);
            }
        } else if (basechain.equals(KAVA_MAIN.INSTANCE)) {
            if (txType == CONST_PW_TX_SIMPLE_SEND) {
                return new BigDecimal(KAVA_GAS_AMOUNT_SEND);
            } else if (txType == CONST_PW_TX_SIMPLE_DELEGATE) {
                return new BigDecimal(KAVA_GAS_AMOUNT_STAKE);
            } else if (txType == CONST_PW_TX_SIMPLE_UNDELEGATE) {
                return new BigDecimal(KAVA_GAS_AMOUNT_STAKE);
            } else if (txType == CONST_PW_TX_SIMPLE_REDELEGATE) {
                return new BigDecimal(KAVA_GAS_AMOUNT_REDELEGATE);
            } else if (txType == CONST_PW_TX_REINVEST) {
                return new BigDecimal(KAVA_GAS_AMOUNT_REINVEST);
            } else if (txType == CONST_PW_TX_SIMPLE_REWARD) {
                ArrayList<String> rewardGasFees = new ArrayList<String>(Arrays.asList(c.getResources().getStringArray(R.array.gas_multi_reward_kava)));
                return new BigDecimal(rewardGasFees.get(valCnt - 1));
            } else if (txType == CONST_PW_TX_SIMPLE_CHANGE_REWARD_ADDRESS) {
                return new BigDecimal(KAVA_GAS_AMOUNT_REWARD_ADDRESS_CHANGE);
            } else if (txType == CONST_PW_TX_VOTE) {
                return new BigDecimal(KAVA_GAS_AMOUNT_VOTE);
            } else if (txType == CONST_PW_TX_CLAIM_INCENTIVE || txType == CONST_PW_TX_CLAIM_HARVEST_REWARD) {
                return new BigDecimal(KAVA_GAS_AMOUNT_CLAIM_INCENTIVE);
            } else if (txType == CONST_PW_TX_CREATE_CDP || txType == CONST_PW_TX_DEPOSIT_CDP || txType == CONST_PW_TX_WITHDRAW_CDP || txType == CONST_PW_TX_DRAW_DEBT_CDP || txType == CONST_PW_TX_REPAY_CDP) {
                return new BigDecimal(KAVA_GAS_AMOUNT_CDP);
            } else if (txType == CONST_PW_TX_DEPOSIT_HARD || txType == CONST_PW_TX_WITHDRAW_HARD || txType == CONST_PW_TX_BORROW_HARD || txType == CONST_PW_TX_REPAY_HARD) {
                return new BigDecimal(KAVA_GAS_AMOUNT_HARD_POOL);
            } else if (txType == CONST_PW_TX_HTLS_REFUND) {
                return new BigDecimal(KAVA_GAS_AMOUNT_BEP3);
            } else if (txType == CONST_PW_TX_KAVA_SWAP) {
                return new BigDecimal(KAVA_GAS_AMOUNT_SWAP);
            } else if (txType == CONST_PW_TX_KAVA_JOIN_POOL) {
                return new BigDecimal(KAVA_GAS_AMOUNT_JOIN_POOL);
            } else if (txType == CONST_PW_TX_KAVA_EXIT_POOL) {
                return new BigDecimal(KAVA_GAS_AMOUNT_EXIT_POOL);
            } else if (txType == CONST_PW_TX_IBC_TRANSFER) {
                return new BigDecimal(KAVA_GAS_AMOUNT_IBC_SEND);
            }

        } else if (basechain.equals(BAND_MAIN.INSTANCE)) {
            if (txType == CONST_PW_TX_SIMPLE_SEND) {
                return new BigDecimal(BAND_GAS_AMOUNT_SEND);
            } else if (txType == CONST_PW_TX_SIMPLE_DELEGATE) {
                return new BigDecimal(BAND_GAS_AMOUNT_STAKE);
            } else if (txType == CONST_PW_TX_SIMPLE_UNDELEGATE) {
                return new BigDecimal(BAND_GAS_AMOUNT_STAKE);
            } else if (txType == CONST_PW_TX_SIMPLE_REDELEGATE) {
                return new BigDecimal(BAND_GAS_AMOUNT_REDELEGATE);
            } else if (txType == CONST_PW_TX_REINVEST) {
                return new BigDecimal(BAND_GAS_AMOUNT_REINVEST);
            } else if (txType == CONST_PW_TX_SIMPLE_REWARD) {
                ArrayList<String> rewardGasFees = new ArrayList<String>(Arrays.asList(c.getResources().getStringArray(R.array.gas_multi_reward_kava)));
                return new BigDecimal(rewardGasFees.get(valCnt - 1));
            } else if (txType == CONST_PW_TX_SIMPLE_CHANGE_REWARD_ADDRESS) {
                return new BigDecimal(BAND_GAS_AMOUNT_ADDRESS_CHANGE);
            } else if (txType == CONST_PW_TX_VOTE) {
                return new BigDecimal(BAND_GAS_AMOUNT_VOTE);
            } else if (txType == CONST_PW_TX_IBC_TRANSFER) {
                return new BigDecimal(BAND_GAS_AMOUNT_IBC_SEND);
            }

        } else if (basechain.equals(OKEX_MAIN.INSTANCE)) {
            if (txType == CONST_PW_TX_SIMPLE_SEND) {
                return new BigDecimal(OK_GAS_AMOUNT_SEND);
            } else if (txType == CONST_PW_TX_OK_DEPOSIT || txType == CONST_PW_TX_OK_WITHDRAW) {
                return (new BigDecimal(OK_GAS_AMOUNT_STAKE_MUX).multiply(new BigDecimal("" + valCnt))).add(new BigDecimal(BaseConstant.OK_GAS_AMOUNT_STAKE));
            } else if (txType == CONST_PW_TX_OK_DIRECT_VOTE) {
                return (new BigDecimal(OK_GAS_AMOUNT_VOTE_MUX).multiply(new BigDecimal("" + valCnt))).add(new BigDecimal(BaseConstant.OK_GAS_AMOUNT_VOTE));
            }

        } else if (basechain.equals(CERTIK_MAIN.INSTANCE)) {
            if (txType == CONST_PW_TX_SIMPLE_SEND) {
                return new BigDecimal(CERTIK_GAS_AMOUNT_SEND);
            } else if (txType == CONST_PW_TX_SIMPLE_DELEGATE) {
                return new BigDecimal(CERTIK_GAS_AMOUNT_STAKE);
            } else if (txType == CONST_PW_TX_SIMPLE_UNDELEGATE) {
                return new BigDecimal(CERTIK_GAS_AMOUNT_STAKE);
            } else if (txType == CONST_PW_TX_SIMPLE_REDELEGATE) {
                return new BigDecimal(CERTIK_GAS_AMOUNT_REDELEGATE);
            } else if (txType == CONST_PW_TX_REINVEST) {
                return new BigDecimal(CERTIK_GAS_AMOUNT_REINVEST);
            } else if (txType == CONST_PW_TX_SIMPLE_REWARD) {
                ArrayList<String> rewardGasFees = new ArrayList<String>(Arrays.asList(c.getResources().getStringArray(R.array.gas_multi_reward_kava)));
                return new BigDecimal(rewardGasFees.get(valCnt - 1));
            } else if (txType == CONST_PW_TX_SIMPLE_CHANGE_REWARD_ADDRESS) {
                return new BigDecimal(CERTIK_GAS_AMOUNT_REWARD_ADDRESS_CHANGE);
            } else if (txType == CONST_PW_TX_VOTE) {
                return new BigDecimal(CERTIK_GAS_AMOUNT_VOTE);
            } else if (txType == CONST_PW_TX_IBC_TRANSFER) {
                return new BigDecimal(CERTIK_GAS_AMOUNT_IBC_SEND);
            }

        } else if (basechain.equals(SECRET_MAIN.INSTANCE)) {
            if (txType == CONST_PW_TX_SIMPLE_SEND || txType == CONST_PW_TX_IBC_TRANSFER) {
                return new BigDecimal(SECRET_GAS_AMOUNT_SEND);
            } else if (txType == CONST_PW_TX_SIMPLE_DELEGATE) {
                return new BigDecimal(SECRET_GAS_AMOUNT_STAKE);
            } else if (txType == CONST_PW_TX_SIMPLE_UNDELEGATE) {
                return new BigDecimal(SECRET_GAS_AMOUNT_STAKE);
            } else if (txType == CONST_PW_TX_SIMPLE_REDELEGATE) {
                return new BigDecimal(SECRET_GAS_AMOUNT_REDELEGATE);
            } else if (txType == CONST_PW_TX_REINVEST) {
                return new BigDecimal(SECRET_GAS_AMOUNT_REINVEST);
            } else if (txType == CONST_PW_TX_SIMPLE_REWARD) {
                ArrayList<String> rewardGasFees = new ArrayList<String>(Arrays.asList(c.getResources().getStringArray(R.array.gas_multi_reward_kava)));
                return new BigDecimal(rewardGasFees.get(valCnt - 1));
            } else if (txType == CONST_PW_TX_SIMPLE_CHANGE_REWARD_ADDRESS) {
                return new BigDecimal(SECRET_GAS_AMOUNT_REWARD_ADDRESS_CHANGE);
            } else if (txType == CONST_PW_TX_VOTE) {
                return new BigDecimal(SECRET_GAS_AMOUNT_VOTE);
            }

        } else if (basechain.equals(CHIHUAHUA_MAIN.INSTANCE)) {
            if (txType == CONST_PW_TX_SIMPLE_REWARD) {
                ArrayList<String> rewardGasFees = new ArrayList<String>(Arrays.asList(c.getResources().getStringArray(R.array.gas_multi_reward)));
                return new BigDecimal(rewardGasFees.get(valCnt - 1));
            } else if (txType == CONST_PW_TX_SIMPLE_REDELEGATE) {
                return new BigDecimal(CERTIK_GAS_AMOUNT_REDELEGATE);
            } else if (txType == CONST_PW_TX_REINVEST) {
                return new BigDecimal(CERTIK_GAS_AMOUNT_REINVEST);
            } else {
                return new BigDecimal(V1_GAS_AMOUNT_MID);
            }

        } else if (basechain.equals(SENTINEL_MAIN.INSTANCE) || basechain.equals(FETCHAI_MAIN.INSTANCE) || basechain.equals(KI_MAIN.INSTANCE) || basechain.equals(MEDI_MAIN.INSTANCE) || basechain.equals(SIF_MAIN.INSTANCE)) {
            if (txType == CONST_PW_TX_SIMPLE_SEND || txType == CONST_PW_TX_IBC_TRANSFER) {
                return new BigDecimal(GAS_AMOUNT_SEND);
            } else if (txType == CONST_PW_TX_SIMPLE_DELEGATE) {
                return new BigDecimal(GAS_AMOUNT_STAKE);
            } else if (txType == CONST_PW_TX_SIMPLE_UNDELEGATE) {
                return new BigDecimal(GAS_AMOUNT_STAKE);
            } else if (txType == CONST_PW_TX_SIMPLE_REDELEGATE) {
                return new BigDecimal(GAS_AMOUNT_REDELEGATE);
            } else if (txType == CONST_PW_TX_REINVEST) {
                return new BigDecimal(GAS_AMOUNT_REINVEST);
            } else if (txType == CONST_PW_TX_SIMPLE_REWARD) {
                ArrayList<String> rewardGasFees = new ArrayList<String>(Arrays.asList(c.getResources().getStringArray(R.array.gas_multi_reward)));
                return new BigDecimal(rewardGasFees.get(valCnt - 1));
            } else if (txType == CONST_PW_TX_SIMPLE_CHANGE_REWARD_ADDRESS) {
                return new BigDecimal(GAS_AMOUNT_REWARD_ADDRESS_CHANGE);
            } else if (txType == CONST_PW_TX_VOTE) {
                return new BigDecimal(GAS_AMOUNT_VOTE);
            } else if (txType == CONST_PW_TX_SIF_CLAIM_INCENTIVE) {
                return new BigDecimal(SIF_GAS_AMOUNT_CLAIM_INCENTIVE);
            } else if (txType == CONST_PW_TX_SIF_SWAP || txType == CONST_PW_TX_SIF_JOIN_POOL || txType == CONST_PW_TX_SIF_EXIT_POOL) {
                return new BigDecimal(SIF_GAS_AMOUNT_DEX);
            }

        } else if (basechain.equals(INJ_MAIN.INSTANCE)) {
            if (txType == CONST_PW_TX_SIMPLE_SEND) {
                return new BigDecimal(V1_GAS_AMOUNT_MID);
            } else if (txType == CONST_PW_TX_SIMPLE_DELEGATE) {
                return new BigDecimal(V1_GAS_AMOUNT_HIGH);
            } else if (txType == CONST_PW_TX_SIMPLE_UNDELEGATE) {
                return new BigDecimal(V1_GAS_AMOUNT_HIGH);
            } else if (txType == CONST_PW_TX_SIMPLE_REDELEGATE) {
                return new BigDecimal(V1_GAS_AMOUNT_HIGH);
            } else if (txType == CONST_PW_TX_REINVEST) {
                return new BigDecimal(V1_GAS_AMOUNT_HIGH);
            } else if (txType == CONST_PW_TX_SIMPLE_REWARD) {
                ArrayList<String> rewardGasFees = new ArrayList<String>(Arrays.asList(c.getResources().getStringArray(R.array.gas_multi_reward_kava)));
                return new BigDecimal(rewardGasFees.get(valCnt - 1));
            } else if (txType == CONST_PW_TX_SIMPLE_CHANGE_REWARD_ADDRESS) {
                return new BigDecimal(V1_GAS_AMOUNT_MID);
            } else if (txType == CONST_PW_TX_VOTE) {
                return new BigDecimal(V1_GAS_AMOUNT_MID);
            } else if (txType == CONST_PW_TX_IBC_TRANSFER) {
                return new BigDecimal(V1_GAS_AMOUNT_HIGH);
            }

        } else {
            if (txType == CONST_PW_TX_SIMPLE_SEND) {
                return new BigDecimal(V1_GAS_AMOUNT_LOW);
            } else if (txType == CONST_PW_TX_SIMPLE_DELEGATE) {
                return new BigDecimal(V1_GAS_AMOUNT_MID);
            } else if (txType == CONST_PW_TX_SIMPLE_UNDELEGATE) {
                return new BigDecimal(V1_GAS_AMOUNT_MID);
            } else if (txType == CONST_PW_TX_SIMPLE_REDELEGATE) {
                return new BigDecimal(V1_GAS_AMOUNT_HIGH);
            } else if (txType == CONST_PW_TX_REINVEST) {
                return new BigDecimal(V1_GAS_AMOUNT_HIGH);
            } else if (txType == CONST_PW_TX_SIMPLE_REWARD) {
                ArrayList<String> rewardGasFees = new ArrayList<String>(Arrays.asList(c.getResources().getStringArray(R.array.gas_multi_reward)));
                return new BigDecimal(rewardGasFees.get(valCnt - 1));
            } else if (txType == CONST_PW_TX_SIMPLE_CHANGE_REWARD_ADDRESS) {
                return new BigDecimal(V1_GAS_AMOUNT_LOW);
            } else if (txType == CONST_PW_TX_VOTE) {
                return new BigDecimal(V1_GAS_AMOUNT_LOW);
            } else if (txType == CONST_PW_TX_GDEX_SWAP) {
                return new BigDecimal(COSMOS_GAS_AMOUNT_SWAP);
            } else if (txType == CONST_PW_TX_GDEX_DEPOSIT) {
                return new BigDecimal(COSMOS_GAS_AMOUNT_JOIN_POOL);
            } else if (txType == CONST_PW_TX_GDEX_WITHDRAW) {
                return new BigDecimal(COSMOS_GAS_AMOUNT_EXIT_POOL);
            } else if (txType == CONST_PW_TX_IBC_TRANSFER) {
                return new BigDecimal(COSMOS_GAS_AMOUNT_IBC_SEND);
            } else if (txType == CONST_PW_TX_MINT_NFT) {
                return new BigDecimal(V1_GAS_AMOUNT_HIGH);
            } else if (txType == CONST_PW_TX_SEND_NFT) {
                return new BigDecimal(V1_GAS_AMOUNT_MID);
            } else if (txType == CONST_PW_TX_PROFILE) {
                return new BigDecimal(V1_GAS_AMOUNT_TOO_HIGH);
            } else if (txType == CONST_PW_TX_LINK_ACCOUNT) {
                return new BigDecimal(V1_GAS_AMOUNT_TOO_HIGH);
            } else if (txType == CONST_PW_TX_EXECUTE_CONTRACT) {
                return new BigDecimal(COSMOS_GAS_AMOUNT_EXECUTE_CONTRACT);
            }
        }
        return result;
    }

    /**
     * Chain Gas Rate
     */
    public static BigDecimal getEstimateGasFeeAmount(Context c, BaseChain basechain, int txType, int valCnt) {
        if (basechain.equals(COSMOS_MAIN.INSTANCE) || basechain.equals(COSMOS_TEST.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(COSMOS_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(IMVERSED_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(IMVERSED_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);
        } else if (basechain.equals(IRIS_MAIN.INSTANCE) || basechain.equals(IRIS_TEST.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(IRIS_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(AKASH_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(COSMOS_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(SENTINEL_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(SENTINEL_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(PERSIS_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(PERSIS_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(CRYPTO_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(CRYPTO_GAS_RATE_TINY);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(OSMOSIS_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(OSMOSIS_GAS_RATE_TINY);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(IOV_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(STARNAME_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(SIF_MAIN.INSTANCE)) {
            return new BigDecimal("100000000000000000");

        } else if (basechain.equals(CERTIK_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(CERTIK_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(BAND_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(BAND_GAS_RATE_TINY);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(MEDI_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(MEDI_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(EMONEY_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(EMONEY_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(FETCHAI_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(FETCH_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(RIZON_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(COSMOS_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(JUNO_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(JUNO_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(REGEN_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(COSMOS_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(BITCANNA_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(BITCANNA_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(ALTHEA_MAIN.INSTANCE) || basechain.equals(ALTHEA_TEST.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(COSMOS_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(STARGAZE_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(STARGAZE_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(GRABRIDGE_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(GRAV_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(KI_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(KI_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(COMDEX_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(COMDEX_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(SECRET_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(SECRET_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(INJ_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(INJ_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(BITSONG_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(BITSONG_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(DESMOS_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(DESMOS_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(LUM_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(LUM_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(CHIHUAHUA_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(CHIHUAHUA_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(KAVA_MAIN.INSTANCE)) {
            if (txType == CONST_PW_TX_HTLS_REFUND) {
                return new BigDecimal("12500");
            }
            BigDecimal gasRate = new BigDecimal(KAVA_GAS_RATE_TINY);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(AXELAR_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(AXELAR_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(KONSTELL_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(KONSTELL_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(UMEE_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(UMEE_GAS_RATE_TINY);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(EVMOS_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(EVMOS_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(CUDOS_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(CUDOS_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(PROVENANCE_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(PROVENANCE_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(CERBERUS_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(CERBERUS_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(OMNIFLIX_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(OMNIFLIX_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(0, RoundingMode.DOWN);

        } else if (basechain.equals(BNB_MAIN.INSTANCE)) {
            return new BigDecimal(FEE_BNB_SEND).setScale(8);

        } else if (basechain.equals(OKEX_MAIN.INSTANCE)) {
            BigDecimal gasRate = new BigDecimal(OK_GAS_RATE_AVERAGE);
            BigDecimal gasAmount = getEstimateGasAmount(c, basechain, txType, valCnt);
            return gasRate.multiply(gasAmount).setScale(18, RoundingMode.DOWN);
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal getGasRate(BaseChain basechain, int position) {
        if (basechain.equals(COSMOS_MAIN.INSTANCE) || basechain.equals(AKASH_MAIN.INSTANCE) || basechain.equals(RIZON_MAIN.INSTANCE) || basechain.equals(REGEN_MAIN.INSTANCE) ||
                basechain.equals(COSMOS_TEST.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(COSMOS_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(COSMOS_GAS_RATE_LOW);
            }
            return new BigDecimal(COSMOS_GAS_RATE_AVERAGE);

        } else if (basechain.equals(IMVERSED_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(IMVERSED_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(IMVERSED_GAS_RATE_LOW);
            }
            return new BigDecimal(IMVERSED_GAS_RATE_AVERAGE);

        } else if (basechain.equals(IRIS_MAIN.INSTANCE) || basechain.equals(IRIS_TEST.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(IRIS_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(IRIS_GAS_RATE_LOW);
            }
            return new BigDecimal(IRIS_GAS_RATE_AVERAGE);

        } else if (basechain.equals(SENTINEL_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(SENTINEL_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(SENTINEL_GAS_RATE_LOW);
            }
            return new BigDecimal(SENTINEL_GAS_RATE_AVERAGE);

        } else if (basechain.equals(PERSIS_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(PERSIS_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(PERSIS_GAS_RATE_LOW);
            }
            return new BigDecimal(PERSIS_GAS_RATE_AVERAGE);

        } else if (basechain.equals(CRYPTO_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(CRYPTO_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(CRYPTO_GAS_RATE_LOW);
            }
            return new BigDecimal(CRYPTO_GAS_RATE_AVERAGE);

        } else if (basechain.equals(OSMOSIS_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(OSMOSIS_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(OSMOSIS_GAS_RATE_LOW);
            }
            return new BigDecimal(OSMOSIS_GAS_RATE_AVERAGE);

        } else if (basechain.equals(IOV_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(STARNAME_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(STARNAME_GAS_RATE_LOW);
            }
            return new BigDecimal(STARNAME_GAS_RATE_AVERAGE);

        } else if (basechain.equals(SIF_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(SIF_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(SIF_GAS_RATE_LOW);
            }
            return new BigDecimal(SIF_GAS_RATE_AVERAGE);

        } else if (basechain.equals(BAND_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(BAND_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(BAND_GAS_RATE_LOW);
            }
            return new BigDecimal(BAND_GAS_RATE_AVERAGE);

        } else if (basechain.equals(MEDI_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(MEDI_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(MEDI_GAS_RATE_LOW);
            }
            return new BigDecimal(MEDI_GAS_RATE_AVERAGE);

        } else if (basechain.equals(CERTIK_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(CERTIK_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(CERTIK_GAS_RATE_LOW);
            }
            return new BigDecimal(CERTIK_GAS_RATE_AVERAGE);

        } else if (basechain.equals(EMONEY_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(EMONEY_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(EMONEY_GAS_RATE_LOW);
            }
            return new BigDecimal(EMONEY_GAS_RATE_AVERAGE);

        } else if (basechain.equals(FETCHAI_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(FETCH_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(FETCH_GAS_RATE_LOW);
            }
            return new BigDecimal(FETCH_GAS_RATE_AVERAGE);

        } else if (basechain.equals(BITCANNA_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(BITCANNA_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(BITCANNA_GAS_RATE_LOW);
            }
            return new BigDecimal(BITCANNA_GAS_RATE_AVERAGE);

        } else if (basechain.equals(STARGAZE_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(STARGAZE_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(STARGAZE_GAS_RATE_LOW);
            }
            return new BigDecimal(STARGAZE_GAS_RATE_AVERAGE);

        } else if (basechain.equals(KI_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(KI_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(KI_GAS_RATE_LOW);
            }
            return new BigDecimal(KI_GAS_RATE_AVERAGE);

        } else if (basechain.equals(COMDEX_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(COMDEX_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(COMDEX_GAS_RATE_LOW);
            }
            return new BigDecimal(COMDEX_GAS_RATE_AVERAGE);

        } else if (basechain.equals(SECRET_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(SECRET_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(SECRET_GAS_RATE_LOW);
            }
            return new BigDecimal(SECRET_GAS_RATE_AVERAGE);

        } else if (basechain.equals(BITSONG_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(BITSONG_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(BITSONG_GAS_RATE_LOW);
            }
            return new BigDecimal(BITSONG_GAS_RATE_AVERAGE);

        } else if (basechain.equals(INJ_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(INJ_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(INJ_GAS_RATE_LOW);
            }
            return new BigDecimal(INJ_GAS_RATE_AVERAGE);

        } else if (basechain.equals(DESMOS_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(DESMOS_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(DESMOS_GAS_RATE_LOW);
            }
            return new BigDecimal(DESMOS_GAS_RATE_AVERAGE);

        } else if (basechain.equals(GRABRIDGE_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(GRAV_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(GRAV_GAS_RATE_LOW);
            }
            return new BigDecimal(GRAV_GAS_RATE_AVERAGE);

        } else if (basechain.equals(LUM_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(LUM_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(LUM_GAS_RATE_LOW);
            }
            return new BigDecimal(LUM_GAS_RATE_AVERAGE);

        } else if (basechain.equals(CHIHUAHUA_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(CHIHUAHUA_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(CHIHUAHUA_GAS_RATE_LOW);
            }
            return new BigDecimal(CHIHUAHUA_GAS_RATE_AVERAGE);

        } else if (basechain.equals(KAVA_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(KAVA_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(KAVA_GAS_RATE_LOW);
            }
            return new BigDecimal(KAVA_GAS_RATE_AVERAGE);

        } else if (basechain.equals(JUNO_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(JUNO_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(JUNO_GAS_RATE_LOW);
            }
            return new BigDecimal(JUNO_GAS_RATE_AVERAGE);

        } else if (basechain.equals(AXELAR_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(AXELAR_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(AXELAR_GAS_RATE_LOW);
            }
            return new BigDecimal(AXELAR_GAS_RATE_AVERAGE);

        } else if (basechain.equals(KONSTELL_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(KONSTELL_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(KONSTELL_GAS_RATE_LOW);
            }
            return new BigDecimal(KONSTELL_GAS_RATE_AVERAGE);

        } else if (basechain.equals(UMEE_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(UMEE_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(UMEE_GAS_RATE_LOW);
            }
            return new BigDecimal(UMEE_GAS_RATE_AVERAGE);

        } else if (basechain.equals(EVMOS_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(EVMOS_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(EVMOS_GAS_RATE_LOW);
            }
            return new BigDecimal(EVMOS_GAS_RATE_AVERAGE);

        } else if (basechain.equals(CUDOS_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(CUDOS_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(CUDOS_GAS_RATE_LOW);
            }
            return new BigDecimal(CUDOS_GAS_RATE_AVERAGE);

        } else if (basechain.equals(PROVENANCE_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(PROVENANCE_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(PROVENANCE_GAS_RATE_LOW);
            }
            return new BigDecimal(PROVENANCE_GAS_RATE_AVERAGE);

        } else if (basechain.equals(CERBERUS_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(CERBERUS_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(CERBERUS_GAS_RATE_LOW);
            }
            return new BigDecimal(CERBERUS_GAS_RATE_AVERAGE);

        } else if (basechain.equals(OMNIFLIX_MAIN.INSTANCE)) {
            if (position == 0) {
                return new BigDecimal(OMNIFLIX_GAS_RATE_TINY);
            } else if (position == 1) {
                return new BigDecimal(OMNIFLIX_GAS_RATE_LOW);
            }
            return new BigDecimal(OMNIFLIX_GAS_RATE_AVERAGE);

        } else if (basechain.equals(BNB_MAIN.INSTANCE)) {
            return BigDecimal.ZERO.setScale(3);

        } else if (basechain.equals(OKEX_MAIN.INSTANCE)) {
            return new BigDecimal(OK_GAS_RATE_AVERAGE);

        }
        return BigDecimal.ZERO.setScale(3);
    }


    /**
     * About Kava
     */
    public static boolean isBep3Coin(String denom) {
        if (denom.equals(TOKEN_HTLC_BINANCE_BNB)) {
            return true;
        }
        if (denom.equals(TOKEN_HTLC_BINANCE_BTCB)) {
            return true;
        }
        if (denom.equals(TOKEN_HTLC_BINANCE_XRPB)) {
            return true;
        }
        if (denom.equals(TOKEN_HTLC_BINANCE_BUSD)) {
            return true;
        }

        if (denom.equals(TOKEN_HTLC_KAVA_BNB)) {
            return true;
        }
        if (denom.equals(TOKEN_HTLC_KAVA_BTCB)) {
            return true;
        }
        if (denom.equals(TOKEN_HTLC_KAVA_XRPB)) {
            return true;
        }
        if (denom.equals(TOKEN_HTLC_KAVA_BUSD)) {
            return true;
        }

        if (denom.equals(TOKEN_HTLC_BINANCE_TEST_BNB)) {
            return true;
        }
        if (denom.equals(TOKEN_HTLC_BINANCE_TEST_BTC)) {
            return true;
        }

        if (denom.equals(TOKEN_HTLC_KAVA_TEST_BNB)) {
            return true;
        }
        return denom.equals(TOKEN_HTLC_KAVA_TEST_BTC);
    }

//    //parse & check vesting account
//    public static void onParseVestingAccount(BaseData baseData, BaseChain baseChain, List<WalletBalance> balances) {
//        WLog.w("onParseVestingAccount");
//        Any account = baseData.mGRpcAccount;
//        if (account == null) return;
//        if (account.getTypeUrl().contains(Vesting.PeriodicVestingAccount.getDescriptor().getFullName())) {
//            Vesting.PeriodicVestingAccount vestingAccount = null;
//            try {
//                vestingAccount = Vesting.PeriodicVestingAccount.parseFrom(account.getValue());
//            } catch (InvalidProtocolBufferException e) {
//                WLog.e("onParseVestingAccount " + e.getMessage());
//                return;
//            }
//            for (WalletBalance balance : balances) {
//                String denom = balance.getSymbol();
//                BigDecimal dpBalance = BigDecimal.ZERO;
//                BigDecimal dpVesting = BigDecimal.ZERO;
//                BigDecimal originalVesting = BigDecimal.ZERO;
//                BigDecimal remainVesting = BigDecimal.ZERO;
//                BigDecimal delegatedVesting = BigDecimal.ZERO;
//
//                dpBalance = balance.balance;
//                WLog.w("dpBalance " + denom + "  " + dpBalance);
//
//                for (CoinOuterClass.Coin vesting : vestingAccount.getBaseVestingAccount().getOriginalVestingList()) {
//                    if (vesting.getDenom().equals(denom)) {
//                        originalVesting = originalVesting.add(new BigDecimal(vesting.getAmount()));
//                    }
//                }
//                WLog.w("originalVesting " + denom + "  " + originalVesting);
//
//                for (CoinOuterClass.Coin vesting : vestingAccount.getBaseVestingAccount().getDelegatedVestingList()) {
//                    if (vesting.getDenom().equals(denom)) {
//                        delegatedVesting = delegatedVesting.add(new BigDecimal(vesting.getAmount()));
//                    }
//                }
//                WLog.w("delegatedVesting " + denom + "  " + delegatedVesting);
//
//                remainVesting = WDp.onParsePeriodicRemainVestingsAmountByDenom(vestingAccount, denom);
//                WLog.w("remainVesting " + denom + "  " + remainVesting);
//
//                dpVesting = remainVesting.subtract(delegatedVesting);
//                WLog.w("dpVestingA " + denom + "  " + dpVesting);
//
//                dpVesting = dpVesting.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : dpVesting;
//                WLog.w("dpVestingB " + denom + "  " + dpVesting);
//
//                if (remainVesting.compareTo(delegatedVesting) > 0) {
//                    dpBalance = dpBalance.subtract(remainVesting).add(delegatedVesting);
//                }
//                WLog.w("final dpBalance  " + denom + "  " + dpBalance);
//
//                if (dpVesting.compareTo(BigDecimal.ZERO) > 0) {
//                    Coin vestingCoin = new Coin(denom, dpVesting.toPlainString());
//                    baseData.mGrpcVesting.add(vestingCoin);
//                    int replace = -1;
//                    for (int i = 0; i < balances.size(); i++) {
//                        if (balances.get(i).getSymbol().equals(denom)) {
//                            replace = i;
//                        }
//                    }
//                    if (replace >= 0) {
//                        balances.set(replace, new Balance(denom, dpBalance.toPlainString()));
//                    }
//                }
//            }
//
//        } else if (account.getTypeUrl().contains(Vesting.ContinuousVestingAccount.getDescriptor().getFullName())) {
//            Vesting.ContinuousVestingAccount vestingAccount = null;
//            try {
//                vestingAccount = Vesting.ContinuousVestingAccount.parseFrom(account.getValue());
//            } catch (InvalidProtocolBufferException e) {
//                WLog.e("onParseVestingAccount " + e.getMessage());
//                return;
//            }
//            for (WalletBalance balance : balances) {
//                String denom = balance.getSymbol();
//                BigDecimal dpBalance = BigDecimal.ZERO;
//                BigDecimal dpVesting = BigDecimal.ZERO;
//                BigDecimal originalVesting = BigDecimal.ZERO;
//                BigDecimal remainVesting = BigDecimal.ZERO;
//                BigDecimal delegatedVesting = BigDecimal.ZERO;
//                dpBalance = balance.balance;
//                WLog.w("dpBalance " + denom + "  " + dpBalance);
//
//                for (CoinOuterClass.Coin vesting : vestingAccount.getBaseVestingAccount().getOriginalVestingList()) {
//                    if (vesting.getDenom().equals(denom)) {
//                        originalVesting = originalVesting.add(new BigDecimal(vesting.getAmount()));
//                    }
//                }
//                WLog.w("originalVesting " + denom + "  " + originalVesting);
//
//                for (CoinOuterClass.Coin vesting : vestingAccount.getBaseVestingAccount().getDelegatedVestingList()) {
//                    if (vesting.getDenom().equals(denom)) {
//                        delegatedVesting = delegatedVesting.add(new BigDecimal(vesting.getAmount()));
//                    }
//                }
//                WLog.w("delegatedVesting " + denom + "  " + delegatedVesting);
//
//                long cTime = Calendar.getInstance().getTime().getTime();
//                long vestingStart = vestingAccount.getStartTime() * 1000;
//                long vestingEnd = vestingAccount.getBaseVestingAccount().getEndTime() * 1000;
//                if (cTime < vestingStart) {
//                    remainVesting = originalVesting;
//                } else if (cTime > vestingEnd) {
//                    remainVesting = BigDecimal.ZERO;
//                } else if (cTime < vestingEnd) {
//                    float progress = ((float) (cTime - vestingStart) / (float) (vestingEnd - vestingStart));
//                    remainVesting = originalVesting.multiply(new BigDecimal(1 - progress)).setScale(0, RoundingMode.UP);
//                }
//                WLog.w("remainVesting " + denom + "  " + remainVesting);
//
//                dpVesting = remainVesting.subtract(delegatedVesting);
//                WLog.w("dpVestingA " + denom + "  " + dpVesting);
//
//                dpVesting = dpVesting.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : dpVesting;
//                WLog.w("dpVestingB " + denom + "  " + dpVesting);
//
//                if (remainVesting.compareTo(delegatedVesting) > 0) {
//                    dpBalance = dpBalance.subtract(remainVesting).add(delegatedVesting);
//                }
//                WLog.w("final dpBalance  " + denom + "  " + dpBalance);
//
//                if (dpVesting.compareTo(BigDecimal.ZERO) > 0) {
//                    Coin vestingCoin = new Coin(denom, dpVesting.toPlainString());
//                    baseData.mGrpcVesting.add(vestingCoin);
//                    int replace = -1;
//                    for (int i = 0; i < baseData.mGrpcBalance.size(); i++) {
//                        if (baseData.mGrpcBalance.get(i).denom.equals(denom)) {
//                            replace = i;
//                        }
//                    }
//                    if (replace >= 0) {
//                        baseData.mGrpcBalance.set(replace, new Coin(denom, dpBalance.toPlainString()));
//                    }
//                }
//            }
//
//        } else if (account.getTypeUrl().contains(Vesting.DelayedVestingAccount.getDescriptor().getFullName())) {
//            Vesting.DelayedVestingAccount vestingAccount = null;
//            try {
//                vestingAccount = Vesting.DelayedVestingAccount.parseFrom(account.getValue());
//            } catch (InvalidProtocolBufferException e) {
//                WLog.e("onParseVestingAccount " + e.getMessage());
//                return;
//            }
//            for (WalletBalance balance : balances) {
//                String denom = balance.getSymbol();
//                BigDecimal dpBalance = BigDecimal.ZERO;
//                BigDecimal dpVesting = BigDecimal.ZERO;
//                BigDecimal originalVesting = BigDecimal.ZERO;
//                BigDecimal remainVesting = BigDecimal.ZERO;
//                BigDecimal delegatedVesting = BigDecimal.ZERO;
//                dpBalance = balance.balance;
//                WLog.w("dpBalance " + denom + "  " + dpBalance);
//
//                for (CoinOuterClass.Coin vesting : vestingAccount.getBaseVestingAccount().getOriginalVestingList()) {
//                    if (vesting.getDenom().equals(denom)) {
//                        originalVesting = originalVesting.add(new BigDecimal(vesting.getAmount()));
//                    }
//                }
//                WLog.w("originalVesting " + denom + "  " + originalVesting);
//
//                long cTime = Calendar.getInstance().getTime().getTime();
//                long vestingEnd = vestingAccount.getBaseVestingAccount().getEndTime() * 1000;
//                if (cTime < vestingEnd) {
//                    remainVesting = originalVesting;
//                }
//                WLog.w("remainVesting " + denom + "  " + remainVesting);
//
//                if (balance.getSymbol().equalsIgnoreCase(baseChain.getMainDenom())) {
//                    BigDecimal stakedAmount = baseData.getDelegationSum();
//                    if (remainVesting.compareTo(stakedAmount) >= 0) {
//                        delegatedVesting = stakedAmount;
//                    } else {
//                        delegatedVesting = remainVesting;
//                    }
//                }
//
//                WLog.w("delegatedVesting " + denom + "  " + delegatedVesting);
//
//                dpVesting = remainVesting.subtract(delegatedVesting);
//                WLog.w("dpVestingA " + denom + "  " + dpVesting);
//
//                dpVesting = dpVesting.compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO : dpVesting;
//                WLog.w("dpVestingB " + denom + "  " + dpVesting);
//
//                if (remainVesting.compareTo(delegatedVesting) > 0) {
//                    dpBalance = dpBalance.subtract(remainVesting).add(delegatedVesting);
//                }
//                WLog.w("final dpBalance  " + denom + "  " + dpBalance);
//
//                if (dpVesting.compareTo(BigDecimal.ZERO) > 0) {
//                    Coin vestingCoin = new Coin(denom, dpVesting.toPlainString());
//                    baseData.mGrpcVesting.add(vestingCoin);
//                    int replace = -1;
//                    for (int i = 0; i < baseData.mGrpcBalance.size(); i++) {
//                        if (baseData.mGrpcBalance.get(i).denom.equals(denom)) {
//                            replace = i;
//                        }
//                    }
//                    if (replace >= 0) {
//                        baseData.mGrpcBalance.set(replace, new Coin(denom, dpBalance.toPlainString()));
//                    }
//                }
//            }
//        }
//    }
}
