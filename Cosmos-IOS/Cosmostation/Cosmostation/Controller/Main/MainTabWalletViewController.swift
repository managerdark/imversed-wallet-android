//
//  MainTabWalletViewController.swift
//  Cosmostation
//
//  Created by yongjoo on 27/09/2019.
//  Copyright © 2019 wannabit. All rights reserved.
//

import UIKit
import Alamofire
import Floaty
import SafariServices

class MainTabWalletViewController: BaseViewController, UITableViewDelegate, UITableViewDataSource, FloatyDelegate {
    
    @IBOutlet weak var titleChainImg: UIImageView!
    @IBOutlet weak var titleWalletName: UILabel!
    @IBOutlet weak var titleChainName: UILabel!
    
    @IBOutlet weak var walletTableView: UITableView!
    var refresher: UIRefreshControl!
    
    var mainTabVC: MainTabViewController!

    override func viewDidLoad() {
        super.viewDidLoad()
        mainTabVC = (self.parent)?.parent as? MainTabViewController
        chainType = WUtils.getChainType(mainTabVC.mAccount.account_base_chain)
        
        self.walletTableView.delegate = self
        self.walletTableView.dataSource = self
        self.walletTableView.separatorStyle = UITableViewCell.SeparatorStyle.none
        self.walletTableView.register(UINib(nibName: "WalletAddressCell", bundle: nil), forCellReuseIdentifier: "WalletAddressCell")
        self.walletTableView.register(UINib(nibName: "WalletCosmosCell", bundle: nil), forCellReuseIdentifier: "WalletCosmosCell")
        self.walletTableView.register(UINib(nibName: "WalletIrisCell", bundle: nil), forCellReuseIdentifier: "WalletIrisCell")
        self.walletTableView.register(UINib(nibName: "WalletBnbCell", bundle: nil), forCellReuseIdentifier: "WalletBnbCell")
        self.walletTableView.register(UINib(nibName: "WalletPriceCell", bundle: nil), forCellReuseIdentifier: "WalletPriceCell")
        self.walletTableView.register(UINib(nibName: "WalletInflationCell", bundle: nil), forCellReuseIdentifier: "WalletInflationCell")
        self.walletTableView.register(UINib(nibName: "WalletGuideCell", bundle: nil), forCellReuseIdentifier: "WalletGuideCell")
        
        refresher = UIRefreshControl()
        refresher.addTarget(self, action: #selector(onRequestFetch), for: .valueChanged)
        refresher.tintColor = UIColor.white
        walletTableView.addSubview(refresher)
        
        self.updateTitle()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.navigationController?.setNavigationBarHidden(true, animated: animated)
        self.navigationController?.navigationBar.topItem?.title = "";
        NotificationCenter.default.addObserver(self, selector: #selector(self.onFetchDone(_:)), name: Notification.Name("onFetchDone"), object: nil)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        self.updateFloaty()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillAppear(animated)
        NotificationCenter.default.removeObserver(self, name: Notification.Name("onFetchDone"), object: nil)
    }
    
    
    func updateTitle() {
        titleChainName.textColor = WUtils.getChainColor(chainType!)
        if (mainTabVC.mAccount.account_nick_name == "") {
            titleWalletName.text = NSLocalizedString("wallet_dash", comment: "") + String(mainTabVC.mAccount.account_id)
        } else {
            titleWalletName.text = mainTabVC.mAccount.account_nick_name
        }
        if (chainType! == ChainType.SUPPORT_CHAIN_COSMOS_MAIN) {
            titleChainImg.image = UIImage(named: "cosmosWhMain")
            titleChainName.text = "(Cosmos Hub)"
        } else if (chainType! == ChainType.SUPPORT_CHAIN_IRIS_MAIN) {
            titleChainImg.image = UIImage(named: "irisWh")
            titleChainName.text = "(Iris Hub)"
        } else if (chainType! == ChainType.SUPPORT_CHAIN_BINANCE_MAIN) {
            titleChainImg.image = UIImage(named: "binanceChImg")
            titleChainName.text = "(Binance Chain)"
        }
    }
    
    func updateFloaty() {
        let floaty = Floaty()
        floaty.buttonImage = UIImage.init(named: "sendImg")
        if (chainType! == ChainType.SUPPORT_CHAIN_COSMOS_MAIN) {
            floaty.buttonColor = COLOR_ATOM
        } else if (chainType! == ChainType.SUPPORT_CHAIN_IRIS_MAIN) {
            floaty.buttonColor = COLOR_IRIS
        } else if (chainType! == ChainType.SUPPORT_CHAIN_BINANCE_MAIN) {
            floaty.buttonColor = COLOR_BNB
        }
        floaty.fabDelegate = self
        self.view.addSubview(floaty)
    }

    
    @objc func onRequestFetch() {
        if(!mainTabVC.onFetchAccountData()) {
            self.refresher.endRefreshing()
        }
    }
    
    
    @objc func onFetchDone(_ notification: NSNotification) {
        self.walletTableView.reloadData()
        self.refresher.endRefreshing()
    }
    
    
    func emptyFloatySelected(_ floaty: Floaty) {
        print("emptyFloatySelected")
        if (!mainTabVC.mAccount.account_has_private) {
//            self.onShowAddMenomicDialog()
            return
        }
    }
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (chainType == ChainType.SUPPORT_CHAIN_COSMOS_MAIN || chainType == ChainType.SUPPORT_CHAIN_IRIS_MAIN) {
            return 5;
        } else if (chainType == ChainType.SUPPORT_CHAIN_BINANCE_MAIN) {
            return 4;
        } else {
            return 0;
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if (chainType == ChainType.SUPPORT_CHAIN_COSMOS_MAIN) {
            return onSetCosmosItems(tableView, indexPath);
        } else if (chainType == ChainType.SUPPORT_CHAIN_IRIS_MAIN) {
            return onSetIrisItem(tableView, indexPath);
        } else {
            return onSetBnbItem(tableView, indexPath);
        }
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if (chainType == ChainType.SUPPORT_CHAIN_COSMOS_MAIN || chainType == ChainType.SUPPORT_CHAIN_IRIS_MAIN) {
            if (indexPath.row == 0) {
                return 78;
            } else if (indexPath.row == 1) {
                return 258;
            } else if (indexPath.row == 2) {
                return 68;
            } else if (indexPath.row == 3) {
                return 68;
            } else if (indexPath.row == 4) {
                return 153;
            }
            
        } else if (chainType == ChainType.SUPPORT_CHAIN_BINANCE_MAIN) {
            if (indexPath.row == 0) {
                return 78;
            } else if (indexPath.row == 1) {
                return 208;
            } else if (indexPath.row == 2) {
                return 68;
            } else if (indexPath.row == 3) {
                return 153;
            }
        }
        return 0;
    }
    
    func onSetCosmosItems(_ tableView: UITableView, _ indexPath: IndexPath) -> UITableViewCell {
        if (indexPath.row == 0) {
            let cell:WalletAddressCell? = tableView.dequeueReusableCell(withIdentifier:"WalletAddressCell") as? WalletAddressCell
            if (mainTabVC.mAccount.account_has_private) {
                cell?.keyState.image = cell?.keyState.image?.withRenderingMode(.alwaysTemplate)
                cell?.keyState.tintColor = COLOR_ATOM
            }
            cell?.dpAddress.text = mainTabVC.mAccount.account_address
            cell?.dpAddress.adjustsFontSizeToFitWidth = true
            cell?.actionShare = {
                self.onClickActionShare()
            }
            cell?.actionWebLink = {
                self.onClickActionLink()
            }
            return cell!
            
        } else if (indexPath.row == 1) {
            let cell:WalletCosmosCell? = tableView.dequeueReusableCell(withIdentifier:"WalletCosmosCell") as? WalletCosmosCell
            cell?.totalAmount.attributedText = WUtils.dpAllAtom(mainTabVC.mBalances, mainTabVC.mBondingList, mainTabVC.mUnbondingList, mainTabVC.mRewardList, mainTabVC.mAllValidator, cell!.totalAmount.font, 6, chainType!)
            cell?.totalValue.attributedText = WUtils.dpAllAtomValue(mainTabVC.mBalances, mainTabVC.mBondingList, mainTabVC.mUnbondingList, mainTabVC.mRewardList, mainTabVC.mAllValidator, mainTabVC.mPriceTic?.value(forKeyPath: getPricePath()) as? Double, cell!.totalAmount.font)
            cell?.availableAmount.attributedText = WUtils.dpTokenAvailable(mainTabVC.mBalances, cell!.availableAmount.font, 6, COSMOS_MAIN_DENOM, chainType!)
            cell?.delegatedAmount.attributedText = WUtils.dpDeleagted(mainTabVC.mBondingList, mainTabVC.mAllValidator, cell!.delegatedAmount.font, 6, chainType!)
            cell?.unbondingAmount.attributedText = WUtils.dpUnbondings(mainTabVC.mUnbondingList, cell!.unbondingAmount.font, 6, chainType!)
            cell?.rewardAmount.attributedText = WUtils.dpRewards(mainTabVC.mRewardList, cell!.rewardAmount.font, 6, COSMOS_MAIN_DENOM, chainType!)
            cell?.actionDelegate = {
                self.onClickValidatorList()
            }
            cell?.actionVote = {
                self.onClickVoteList()
            }
            return cell!
            
        } else if (indexPath.row == 2) {
            let cell:WalletPriceCell? = tableView.dequeueReusableCell(withIdentifier:"WalletPriceCell") as? WalletPriceCell
            cell?.sourceSite.text = "("+BaseData.instance.getMarketString()+")"
            cell?.perPrice.attributedText = WUtils.dpPricePerUnit(mainTabVC.mPriceTic?.value(forKeyPath: getPricePath()) as? Double, cell!.perPrice.font)
            let changeValue = WUtils.priceChanges(mainTabVC.mPriceTic?.value(forKeyPath: getPrice24hPath()) as? Double)
            if (changeValue.compare(NSDecimalNumber.zero).rawValue > 0) {
                cell?.updownImg.image = UIImage(named: "priceUp")
                cell?.updownPercent.attributedText = WUtils.displayPriceUPdown(changeValue, font: cell!.updownPercent.font)
            } else if (changeValue.compare(NSDecimalNumber.zero).rawValue < 0) {
                cell?.updownImg.image = UIImage(named: "priceDown")
                cell?.updownPercent.attributedText = WUtils.displayPriceUPdown(changeValue, font: cell!.updownPercent.font)
            } else {
                cell?.updownImg.image = nil
                cell?.updownPercent.text = ""
            }
            return cell!
            
        } else if (indexPath.row == 3) {
            let cell:WalletInflationCell? = tableView.dequeueReusableCell(withIdentifier:"WalletInflationCell") as? WalletInflationCell
            if (mainTabVC!.mInflation != nil) {
                cell?.infaltionLabel.attributedText = WUtils.displayInflation(NSDecimalNumber.init(string: mainTabVC.mInflation), font: cell!.infaltionLabel.font)
            }
            if (mainTabVC!.mStakingPool != nil && mainTabVC!.mProvision != nil) {
                cell?.yieldLabel.attributedText = WUtils.displayYield(NSDecimalNumber.init(string: mainTabVC.mStakingPool?.object(forKey: "bonded_tokens") as? String), NSDecimalNumber.init(string: mainTabVC.mProvision), NSDecimalNumber.zero, font: cell!.yieldLabel.font)
            }
            return cell!
            
        } else {
            let cell:WalletGuideCell? = tableView.dequeueReusableCell(withIdentifier:"WalletGuideCell") as? WalletGuideCell
            cell?.guideImg.image = UIImage(named: "guideImg")
            cell?.guideTitle.text = NSLocalizedString("send_guide_title_cosmos", comment: "")
            cell?.guideMsg.text = NSLocalizedString("send_guide_msg_cosmos", comment: "")
            cell?.btn1Label.setTitle(NSLocalizedString("send_guide_btn1_cosmos", comment: ""), for: .normal)
            cell?.btn2Label.setTitle(NSLocalizedString("send_guide_btn2_cosmos", comment: ""), for: .normal)
            cell?.actionGuide1 = {
                print("click actionGuide1")
            }
            cell?.actionGuide2 = {
                print("click actionGuide2")
            }
            return cell!
        }
    }
    
    func onSetIrisItem(_ tableView: UITableView, _ indexPath: IndexPath)  -> UITableViewCell {
        if (indexPath.row == 0) {
            let cell:WalletAddressCell? = tableView.dequeueReusableCell(withIdentifier:"WalletAddressCell") as? WalletAddressCell
            if (mainTabVC.mAccount.account_has_private) {
                cell?.keyState.image = cell?.keyState.image?.withRenderingMode(.alwaysTemplate)
                cell?.keyState.tintColor = COLOR_IRIS
            }
            cell?.dpAddress.text = mainTabVC.mAccount.account_address
            cell?.dpAddress.adjustsFontSizeToFitWidth = true
            cell?.actionShare = {
                self.onClickActionShare()
            }
            cell?.actionWebLink = {
                self.onClickActionLink()
            }
            return cell!
            
        } else if (indexPath.row == 1) {
            let cell:WalletIrisCell? = tableView.dequeueReusableCell(withIdentifier:"WalletIrisCell") as? WalletIrisCell
            cell?.totalAmount.attributedText = WUtils.dpAllIris(mainTabVC.mBalances, mainTabVC.mBondingList, mainTabVC.mUnbondingList, mainTabVC.mIrisRewards, mainTabVC.mAllValidator, cell!.totalAmount.font, 6, chainType!)
            cell?.totalValue.attributedText = WUtils.dpAllIrisValue(mainTabVC.mBalances, mainTabVC.mBondingList, mainTabVC.mUnbondingList, mainTabVC.mIrisRewards, mainTabVC.mAllValidator, mainTabVC.mPriceTic?.value(forKeyPath: getPricePath()) as? Double, cell!.totalAmount.font)
            cell?.availableAmount.attributedText = WUtils.dpTokenAvailable(mainTabVC.mBalances, cell!.availableAmount.font, 6, IRIS_MAIN_DENOM, chainType!)
            cell?.delegatedAmount.attributedText = WUtils.dpDeleagted(mainTabVC.mBondingList, mainTabVC.mAllValidator, cell!.delegatedAmount.font, 6, chainType!)
            cell?.unbondingAmount.attributedText = WUtils.dpUnbondings(mainTabVC.mUnbondingList, cell!.unbondingAmount.font, 6, chainType!)
            cell?.rewardAmount.attributedText = WUtils.dpIrisRewards(mainTabVC.mIrisRewards, cell!.rewardAmount.font, 6, chainType!)
            cell?.actionDelegate = {
                self.onClickValidatorList()
            }
            cell?.actionVote = {
                self.onClickVoteList()
            }
            return cell!
            
        } else if (indexPath.row == 2) {
            let cell:WalletPriceCell? = tableView.dequeueReusableCell(withIdentifier:"WalletPriceCell") as? WalletPriceCell
            cell?.sourceSite.text = "("+BaseData.instance.getMarketString()+")"
            cell?.perPrice.attributedText = WUtils.dpPricePerUnit(mainTabVC.mPriceTic?.value(forKeyPath: getPricePath()) as? Double, cell!.perPrice.font)
            let changeValue = WUtils.priceChanges(mainTabVC.mPriceTic?.value(forKeyPath: getPrice24hPath()) as? Double)
            if (changeValue.compare(NSDecimalNumber.zero).rawValue > 0) {
                cell?.updownImg.image = UIImage(named: "priceUp")
                cell?.updownPercent.attributedText = WUtils.displayPriceUPdown(changeValue, font: cell!.updownPercent.font)
            } else if (changeValue.compare(NSDecimalNumber.zero).rawValue < 0) {
                cell?.updownImg.image = UIImage(named: "priceDown")
                cell?.updownPercent.attributedText = WUtils.displayPriceUPdown(changeValue, font: cell!.updownPercent.font)
            } else {
                cell?.updownImg.image = nil
                cell?.updownPercent.text = ""
            }
            return cell!
            
        } else if (indexPath.row == 3) {
            let cell:WalletInflationCell? = tableView.dequeueReusableCell(withIdentifier:"WalletInflationCell") as? WalletInflationCell
            cell?.infaltionLabel.attributedText = WUtils.displayInflation(NSDecimalNumber.init(string: "0.04"), font: cell!.infaltionLabel.font)
            if (mainTabVC!.mIrisStakePool != nil) {
                let provisions = NSDecimalNumber.init(string: mainTabVC.mIrisStakePool?.object(forKey: "total_supply") as? String).multiplying(by: NSDecimalNumber.init(string: "0.04"))
                let bonded_tokens = NSDecimalNumber.init(string: mainTabVC.mIrisStakePool?.object(forKey: "bonded_tokens") as? String)
                cell?.yieldLabel.attributedText = WUtils.displayYield(bonded_tokens, provisions, NSDecimalNumber.zero, font: cell!.yieldLabel.font)
            }
            return cell!
            
        } else {
            let cell:WalletGuideCell? = tableView.dequeueReusableCell(withIdentifier:"WalletGuideCell") as? WalletGuideCell
            cell?.guideImg.image = UIImage(named: "irisnetImg")
            cell?.guideTitle.text = NSLocalizedString("send_guide_title_iris", comment: "")
            cell?.guideMsg.text = NSLocalizedString("send_guide_msg_iris", comment: "")
            cell?.btn1Label.setTitle(NSLocalizedString("send_guide_btn1_iris", comment: ""), for: .normal)
            cell?.btn2Label.setTitle(NSLocalizedString("send_guide_btn2_iris", comment: ""), for: .normal)
            cell?.actionGuide1 = {
                print("click actionGuide1")
            }
            cell?.actionGuide2 = {
                print("click actionGuide2")
            }
            return cell!
        }
        
    }
    
    func onSetBnbItem(_ tableView: UITableView, _ indexPath: IndexPath)  -> UITableViewCell {
        if (indexPath.row == 0) {
            let cell:WalletAddressCell? = tableView.dequeueReusableCell(withIdentifier:"WalletAddressCell") as? WalletAddressCell
            if (mainTabVC.mAccount.account_has_private) {
                cell?.keyState.image = cell?.keyState.image?.withRenderingMode(.alwaysTemplate)
                cell?.keyState.tintColor = COLOR_BNB
            }
            cell?.dpAddress.text = mainTabVC.mAccount.account_address
            cell?.dpAddress.adjustsFontSizeToFitWidth = true
            cell?.actionShare = {
                self.onClickActionShare()
            }
            cell?.actionWebLink = {
                self.onClickActionLink()
            }
            return cell!
            
        } else if (indexPath.row == 1) {
            let cell:WalletBnbCell? = tableView.dequeueReusableCell(withIdentifier:"WalletBnbCell") as? WalletBnbCell
            if let balance = WUtils.getTokenBalace(mainTabVC.mBalances, BNB_MAIN_DENOM) {
                let totalAmount = WUtils.stringToDecimal(balance.balance_amount).adding(WUtils.stringToDecimal(balance.balance_locked!))
                cell?.totalAmount.attributedText = WUtils.displayAmount(totalAmount.stringValue, cell!.totalAmount.font, 6, chainType!)
                cell?.totalValue.attributedText = WUtils.dpBnbValue(totalAmount, mainTabVC.mPriceTic?.value(forKeyPath: getPricePath()) as? Double, cell!.totalAmount.font)
                cell?.availableAmount.attributedText = WUtils.displayAmount(balance.balance_amount, cell!.availableAmount.font, 6, chainType!)
                cell?.lockedAmount.attributedText = WUtils.displayAmount(balance.balance_locked!, cell!.lockedAmount.font, 6, chainType!)
                cell?.actionWC = {
                    print("click action WC")
                }
            }
            return cell!
            
        } else if (indexPath.row == 2) {
            let cell:WalletPriceCell? = tableView.dequeueReusableCell(withIdentifier:"WalletPriceCell") as? WalletPriceCell
            cell?.sourceSite.text = "("+BaseData.instance.getMarketString()+")"
            cell?.perPrice.attributedText = WUtils.dpPricePerUnit(mainTabVC.mPriceTic?.value(forKeyPath: getPricePath()) as? Double, cell!.perPrice.font)
            let changeValue = WUtils.priceChanges(mainTabVC.mPriceTic?.value(forKeyPath: getPrice24hPath()) as? Double)
            if (changeValue.compare(NSDecimalNumber.zero).rawValue > 0) {
                cell?.updownImg.image = UIImage(named: "priceUp")
                cell?.updownPercent.attributedText = WUtils.displayPriceUPdown(changeValue, font: cell!.updownPercent.font)
            } else if (changeValue.compare(NSDecimalNumber.zero).rawValue < 0) {
                cell?.updownImg.image = UIImage(named: "priceDown")
                cell?.updownPercent.attributedText = WUtils.displayPriceUPdown(changeValue, font: cell!.updownPercent.font)
            } else {
                cell?.updownImg.image = nil
                cell?.updownPercent.text = ""
            }
            return cell!
            
        } else {
            let cell:WalletGuideCell? = tableView.dequeueReusableCell(withIdentifier:"WalletGuideCell") as? WalletGuideCell
            cell?.guideImg.image = UIImage(named: "binanceImg")
            
            cell?.guideTitle.text = NSLocalizedString("send_guide_title_bnb", comment: "")
            cell?.guideMsg.text = NSLocalizedString("send_guide_msg_bnb", comment: "")
            cell?.btn1Label.setTitle(NSLocalizedString("send_guide_btn1_bnb", comment: ""), for: .normal)
            cell?.btn2Label.setTitle(NSLocalizedString("send_guide_btn2_bnb", comment: ""), for: .normal)
            cell?.actionGuide1 = {
                print("click actionGuide1")
            }
            cell?.actionGuide2 = {
                print("click actionGuide2")
            }
            return cell!
        }
        
    }
    
    @IBAction func onClickSwitchAccount(_ sender: Any) {
        self.mainTabVC.dropDown.show()
    }
    
    func onClickActionShare() {
        var nickName:String?
        if (mainTabVC.mAccount.account_nick_name == "") {
            nickName = NSLocalizedString("wallet_dash", comment: "") + String(mainTabVC.mAccount.account_id)
        } else {
            nickName = mainTabVC.mAccount.account_nick_name
        }
        self.shareAddress(mainTabVC.mAccount.account_address, nickName!)
    }
    
    func onClickActionLink() {
        if (chainType! == ChainType.SUPPORT_CHAIN_COSMOS_MAIN) {
            guard let url = URL(string: "https://www.mintscan.io/account/" + mainTabVC.mAccount.account_address) else { return }
            let safariViewController = SFSafariViewController(url: url)
            present(safariViewController, animated: true, completion: nil)
            
        } else if (chainType! == ChainType.SUPPORT_CHAIN_IRIS_MAIN) {
            guard let url = URL(string: "https://irishub.mintscan.io/account/" + mainTabVC.mAccount.account_address) else { return }
            let safariViewController = SFSafariViewController(url: url)
            present(safariViewController, animated: true, completion: nil)
            
        } else if (chainType! == ChainType.SUPPORT_CHAIN_BINANCE_MAIN) {
            guard let url = URL(string: "https://explorer.binance.org/address/" + mainTabVC.mAccount.account_address) else { return }
            let safariViewController = SFSafariViewController(url: url)
            present(safariViewController, animated: true, completion: nil)
        }
    }
    
    func onClickValidatorList() {
        print("onClickValidatorList")
        let validatorListVC = UIStoryboard(name: "MainStoryboard", bundle: nil).instantiateViewController(withIdentifier: "ValidatorListViewController") as! ValidatorListViewController
        validatorListVC.hidesBottomBarWhenPushed = true
        self.navigationItem.title = ""
        self.navigationController?.pushViewController(validatorListVC, animated: true)
        
    }
    
    func onClickVoteList() {
        print("onClickVoteList")
        let voteListVC = UIStoryboard(name: "MainStoryboard", bundle: nil).instantiateViewController(withIdentifier: "VoteListViewController") as! VoteListViewController
        voteListVC.hidesBottomBarWhenPushed = true
        self.navigationItem.title = ""
        self.navigationController?.pushViewController(voteListVC, animated: true)
        
    }
    
    func onClickWalletConect() {
        print("onClickWalletConect")
        
    }
    
    func onClickGuide1() {
        print("onClickGuide1")
        
    }
    
    func onClickGuide2() {
        print("onClickGuide2")
        
    }
    
    func onClickMainSend() {
        print("onClickMainSend")
        if (chainType! == ChainType.SUPPORT_CHAIN_COSMOS_MAIN) {
            
        } else if (chainType! == ChainType.SUPPORT_CHAIN_IRIS_MAIN) {
            
        } else if (chainType! == ChainType.SUPPORT_CHAIN_BINANCE_MAIN) {
            
        }
        
    }
    
    
    
}
