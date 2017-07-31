package com.tlo.specialist.util;

import java.net.URL;
import java.util.Properties;

import com.tlo.specialist.scraper.CompanyContactInfoScraper;
import com.tlo.specialist.scraper.impl.ABBContactInfoScraper;
import com.tlo.specialist.scraper.impl.AXAGroupContactInfoScraper;
import com.tlo.specialist.scraper.impl.AccentureContactInfoScraper;
import com.tlo.specialist.scraper.impl.AecomContactInfoScraper;
import com.tlo.specialist.scraper.impl.AerotekContactInfoScraper;
import com.tlo.specialist.scraper.impl.AgilisysContactInfoScraper;
import com.tlo.specialist.scraper.impl.AkerSolutionsContactInfoScraper;
import com.tlo.specialist.scraper.impl.AlgarTechContactInfoScraper;
import com.tlo.specialist.scraper.impl.AltranContactInfoScraper;
import com.tlo.specialist.scraper.impl.ArcadisContactInfoScraper;
import com.tlo.specialist.scraper.impl.AtlasCopcoContactInfoScraper;
import com.tlo.specialist.scraper.impl.CadenceDesignSystemsContactInfoScraper;
import com.tlo.specialist.scraper.impl.CameronInternationalContactInfoScraper;
import com.tlo.specialist.scraper.impl.ChevronContactInfoScraper;
import com.tlo.specialist.scraper.impl.CintasContactInfoScraper;
import com.tlo.specialist.scraper.impl.CiscoContactInfoScraper;
import com.tlo.specialist.scraper.impl.CreditSuisseContactInfoScraper;
import com.tlo.specialist.scraper.impl.CushmanAndWakefieldContactInfoScraper;
import com.tlo.specialist.scraper.impl.DieboldContactInfoScraper;
import com.tlo.specialist.scraper.impl.EMCContactInfoScraper;
import com.tlo.specialist.scraper.impl.ESRIContactInfoScraper;
import com.tlo.specialist.scraper.impl.EcolabContactInfoScraper;
import com.tlo.specialist.scraper.impl.EniContactInfoScraper;
import com.tlo.specialist.scraper.impl.HenkelContactInfoScraper;
import com.tlo.specialist.scraper.impl.HitachiDataSystemsContactInfoScraper;
import com.tlo.specialist.scraper.impl.IHSContactInfoScraper;
import com.tlo.specialist.scraper.impl.IMSHealthContactInfoScraper;
import com.tlo.specialist.scraper.impl.InformationBuildersContactInfoScraper;
import com.tlo.specialist.scraper.impl.InfosysContactInfoScraper;
import com.tlo.specialist.scraper.impl.IntelCorporationContactInfoScraper;
import com.tlo.specialist.scraper.impl.IntermountainHealthcareContactInfoScraper;
import com.tlo.specialist.scraper.impl.JLLContactInfoScraper;
import com.tlo.specialist.scraper.impl.JohnsonControlsContactInfoScraper;
import com.tlo.specialist.scraper.impl.KPMGContactInfoScraper;
import com.tlo.specialist.scraper.impl.Level3CommunicationsContactInfoScraper;
import com.tlo.specialist.scraper.impl.MacquarieContactInfoScraper;
import com.tlo.specialist.scraper.impl.MapfreContactInfoScraper;
import com.tlo.specialist.scraper.impl.MercerContactInfoScraper;
import com.tlo.specialist.scraper.impl.MultiPagedContactInfoScraper;
import com.tlo.specialist.scraper.impl.MultiPagedOneDropdownClickButtonContactInfoScraper;
import com.tlo.specialist.scraper.impl.MultiPagedOneDropdownContactInfoScraper;
import com.tlo.specialist.scraper.impl.MultiPagedOneLevelListOfLinksContactInfoScraper;
import com.tlo.specialist.scraper.impl.MultiPagedOneLevelListOfLinksSplitByNodesContactInfoScraper;
import com.tlo.specialist.scraper.impl.MultiPagedTwoDropdownsClickButtonContactInfoScraper;
import com.tlo.specialist.scraper.impl.MultiPagedTwoLevelsListsOfLinksContactInfoScraper;
import com.tlo.specialist.scraper.impl.NECContactInfoScraper;
import com.tlo.specialist.scraper.impl.NTTDataServicesContactInfoScraper;
import com.tlo.specialist.scraper.impl.NationalOilwellVarcoContactInfoScraper;
import com.tlo.specialist.scraper.impl.NetappContactInfoScraper;
import com.tlo.specialist.scraper.impl.OnePagedClickDropdownOptionsContactInfoScraper;
import com.tlo.specialist.scraper.impl.OnePagedClickLinksToDisplayContactInfoScraper;
import com.tlo.specialist.scraper.impl.OnePagedLoadMoreButtonContactInfoScraper;
import com.tlo.specialist.scraper.impl.OnePagedOneDropdownClickButtonContactInfoScraper;
import com.tlo.specialist.scraper.impl.OnePagedOneDropdownContactInfoScraper;
import com.tlo.specialist.scraper.impl.OnePagedSplitNodesContactInfoScraper;
import com.tlo.specialist.scraper.impl.OnePagedTwoDropdownsContactInfoScraper;
import com.tlo.specialist.scraper.impl.PTCCorporationContactInfoScraper;
import com.tlo.specialist.scraper.impl.PWCContactInfoScraper;
import com.tlo.specialist.scraper.impl.ParkerHannifinContactInfoScraper;
import com.tlo.specialist.scraper.impl.PrudentialFinancialContactInfoScraper;
import com.tlo.specialist.scraper.impl.QatarAirwaysContactInfoScraper;
import com.tlo.specialist.scraper.impl.QuintilesIMSContactInfoScraper;
import com.tlo.specialist.scraper.impl.RandstadContactInfoScraper;
import com.tlo.specialist.scraper.impl.RollsRoyceContactInfoScraper;
import com.tlo.specialist.scraper.impl.SAICContactInfoScraper;
import com.tlo.specialist.scraper.impl.SDLContactInfoScraper;
import com.tlo.specialist.scraper.impl.SLLITContactInfoScraper;
import com.tlo.specialist.scraper.impl.SaipemContactInfoScraper;
import com.tlo.specialist.scraper.impl.SanminaContactInfoScraper;
import com.tlo.specialist.scraper.impl.StantecContactInfoScraper;
import com.tlo.specialist.scraper.impl.SutherlandGlobalContactInfoScraper;
import com.tlo.specialist.scraper.impl.TelcomItaliaContactInfoScraper;
import com.tlo.specialist.scraper.impl.TheSalvationArmyContactInfoScraper;
import com.tlo.specialist.scraper.impl.USAAContactInfoScraper;
import com.tlo.specialist.scraper.impl.USTGlobalContactInfoScraper;
import com.tlo.specialist.scraper.impl.UbisoftContactInfoScraper;
import com.tlo.specialist.scraper.impl.UniCreditContactInfoScraper;
import com.tlo.specialist.scraper.impl.WorleyParsonsContactInfoScraper;
import com.tlo.specialist.scraper.impl.YahooContactInfoScraper;
import com.tlo.specialist.scraper.impl.ZurichInsuranceContactInfoScraper;

public class CompanyContactInfoScraperFactory {

	public static CompanyContactInfoScraper getScraper(String masterCompanyId) throws Exception {
		try {
			if ("MC92782".equalsIgnoreCase(masterCompanyId)) {
				return new AccentureContactInfoScraper();
			} else if ("MC28535".equalsIgnoreCase(masterCompanyId)) {
				return new PWCContactInfoScraper();
			} else if ("MC92555".equalsIgnoreCase(masterCompanyId)) {
				return new InfosysContactInfoScraper();
			} else if ("MC30962".equalsIgnoreCase(masterCompanyId)) {
				return new KPMGContactInfoScraper();
			} else if ("MC87291".equalsIgnoreCase(masterCompanyId)) {
				return new IntelCorporationContactInfoScraper();
			} else if ("MC87228".equalsIgnoreCase(masterCompanyId)) {
				return new CiscoContactInfoScraper();
			} else if ("MC93918".equalsIgnoreCase(masterCompanyId)) {
				return new ABBContactInfoScraper();
			} else if ("MC93340".equalsIgnoreCase(masterCompanyId)) {
				return new AXAGroupContactInfoScraper();
			} else if ("MC87270".equalsIgnoreCase(masterCompanyId)) {	
				return new ChevronContactInfoScraper();
			} else if ("MC95046".equalsIgnoreCase(masterCompanyId)) {
				return new AecomContactInfoScraper();
			} else if ("MC88987".equalsIgnoreCase(masterCompanyId)) {
				return new EMCContactInfoScraper();
			} else if ("MC20149".equalsIgnoreCase(masterCompanyId)) {
				return new IMSHealthContactInfoScraper();
			} else if ("MC30624".equalsIgnoreCase(masterCompanyId)) {
				return new YahooContactInfoScraper();
			} else if ("MC13125".equalsIgnoreCase(masterCompanyId)) {
				return new AltranContactInfoScraper();
			} else if ("MC90751".equalsIgnoreCase(masterCompanyId)) {
				return new DieboldContactInfoScraper();
			} else if ("MC87348".equalsIgnoreCase(masterCompanyId)) {
				return new NetappContactInfoScraper();
			} else if ("MC92134".equalsIgnoreCase(masterCompanyId)) {
				return new SAICContactInfoScraper();
			} else if ("MC62478".equalsIgnoreCase(masterCompanyId)) {
				return new USTGlobalContactInfoScraper();
			} else if ("MC113373".equalsIgnoreCase(masterCompanyId)) {
				return new NTTDataServicesContactInfoScraper();
			} else if ("MC92469".equalsIgnoreCase(masterCompanyId)) {
				return new JohnsonControlsContactInfoScraper();
			} else if ("MC93920".equalsIgnoreCase(masterCompanyId)) {
				return new CreditSuisseContactInfoScraper();
			} else if ("MC51846".equalsIgnoreCase(masterCompanyId)) {
				return new JLLContactInfoScraper();
			} else if ("MC92763".equalsIgnoreCase(masterCompanyId)) {
				return new RandstadContactInfoScraper();
			} else if ("MC93199".equalsIgnoreCase(masterCompanyId)) {
				return new UniCreditContactInfoScraper();
			} else if ("MC89705".equalsIgnoreCase(masterCompanyId)) {
				return new QuintilesIMSContactInfoScraper();
			} else if ("MC23839".equalsIgnoreCase(masterCompanyId)) {
				return new CushmanAndWakefieldContactInfoScraper();
			} else if ("MC85171".equalsIgnoreCase(masterCompanyId)) {
				return new TheSalvationArmyContactInfoScraper();
			} else if ("MC93116".equalsIgnoreCase(masterCompanyId)) {
				return new RollsRoyceContactInfoScraper();
			} else if ("MC74001".equalsIgnoreCase(masterCompanyId)) {
				return new PrudentialFinancialContactInfoScraper();
			} else if ("MC92800".equalsIgnoreCase(masterCompanyId)) {
				return new HenkelContactInfoScraper();
			} else if ("MC72977".equalsIgnoreCase(masterCompanyId)) {
				return new MercerContactInfoScraper();
			} else if ("MC91866".equalsIgnoreCase(masterCompanyId)) {
				return new NationalOilwellVarcoContactInfoScraper();
			} else if ("MC91811".equalsIgnoreCase(masterCompanyId)) {
				return new USAAContactInfoScraper();
			} else if ("MC89506".equalsIgnoreCase(masterCompanyId)) {
				return new EcolabContactInfoScraper();
			} else if ("MC6136".equalsIgnoreCase(masterCompanyId)) {
				return new ArcadisContactInfoScraper();
			} else if ("MC104744".equalsIgnoreCase(masterCompanyId)) {
				return new SaipemContactInfoScraper();
			} else if ("MC93432".equalsIgnoreCase(masterCompanyId)) {
				return new EniContactInfoScraper();
			} else if ("MC90632".equalsIgnoreCase(masterCompanyId)) {
				return new ParkerHannifinContactInfoScraper();
			} else if ("MC90488".equalsIgnoreCase(masterCompanyId)) {
				return new SutherlandGlobalContactInfoScraper();
			} else if ("MC99869".equalsIgnoreCase(masterCompanyId)) {
				return new ZurichInsuranceContactInfoScraper();
			} else if ("MC7777".equalsIgnoreCase(masterCompanyId)) {
				return new AerotekContactInfoScraper();
			} else if ("MC104789".equalsIgnoreCase(masterCompanyId)) {
				return new WorleyParsonsContactInfoScraper();
			} else if ("MC41539".equalsIgnoreCase(masterCompanyId)) {
				return new CintasContactInfoScraper();
			} else if ("MC104740".equalsIgnoreCase(masterCompanyId)) {
				return new QatarAirwaysContactInfoScraper();
			} else if ("MC5752".equalsIgnoreCase(masterCompanyId)) {
				return new AkerSolutionsContactInfoScraper();
			} else if ("MC93434".equalsIgnoreCase(masterCompanyId)) {
				return new TelcomItaliaContactInfoScraper();
			} else if ("MC93602".equalsIgnoreCase(masterCompanyId)) {
				return new AtlasCopcoContactInfoScraper();
			} else if ("MC91621".equalsIgnoreCase(masterCompanyId)) {
				return new CameronInternationalContactInfoScraper();
			} else if ("MC87225".equalsIgnoreCase(masterCompanyId)) {
				return new SanminaContactInfoScraper();
			} else if ("MC93158".equalsIgnoreCase(masterCompanyId)) {
				return new MapfreContactInfoScraper();
			} else if ("MC56125".equalsIgnoreCase(masterCompanyId)) {
				return new IntermountainHealthcareContactInfoScraper();
			} else if ("MC33463".equalsIgnoreCase(masterCompanyId)) {
				return new StantecContactInfoScraper();
			} else if ("MC97459".equalsIgnoreCase(masterCompanyId)) {
				return new MacquarieContactInfoScraper();
			} else if ("MC21452".equalsIgnoreCase(masterCompanyId)) {
				return new Level3CommunicationsContactInfoScraper();
			} else if ("MC15182".equalsIgnoreCase(masterCompanyId)) {
				return new CadenceDesignSystemsContactInfoScraper();
			} else if ("MC94004".equalsIgnoreCase(masterCompanyId)) {
				return new HitachiDataSystemsContactInfoScraper();
			} else if ("MC56135".equalsIgnoreCase(masterCompanyId)) {
				return new IHSContactInfoScraper();
			} else if ("MC79589".equalsIgnoreCase(masterCompanyId)) {
				return new PTCCorporationContactInfoScraper();
			} else if ("MC104769".equalsIgnoreCase(masterCompanyId)) {
				return new UbisoftContactInfoScraper();
			} else if ("MC93782".equalsIgnoreCase(masterCompanyId)) {
				return new NECContactInfoScraper();
			} else if ("MC105227".equalsIgnoreCase(masterCompanyId)) {
				return new AlgarTechContactInfoScraper();
			} else if ("MC11988".equalsIgnoreCase(masterCompanyId)) {
				return new ESRIContactInfoScraper();
			} else if ("MC72941".equalsIgnoreCase(masterCompanyId)) {
				return new SDLContactInfoScraper();
			} else if ("MC30128".equalsIgnoreCase(masterCompanyId)) {
				return new InformationBuildersContactInfoScraper();
			} else if ("MC112857".equalsIgnoreCase(masterCompanyId)) {
				return new SLLITContactInfoScraper();
			} else if ("MC100447".equalsIgnoreCase(masterCompanyId)) {
				return new AgilisysContactInfoScraper();
			} else if (isCompanyIncludedIn(masterCompanyId, "multi.paged")) {
				return new MultiPagedContactInfoScraper();
			} else if (isCompanyIncludedIn(masterCompanyId, "multi.paged.one.level.list.links")) {
				return new MultiPagedOneLevelListOfLinksContactInfoScraper();
			} else if (isCompanyIncludedIn(masterCompanyId, "multi.paged.two.level.list.links")) {
				return new MultiPagedTwoLevelsListsOfLinksContactInfoScraper();
			} else if (isCompanyIncludedIn(masterCompanyId, "multi.paged.one.level.list.links.split.nodes")) {
				return new MultiPagedOneLevelListOfLinksSplitByNodesContactInfoScraper();
			} else if (isCompanyIncludedIn(masterCompanyId, "multi.paged.one.dropdown.click.button")) {
				return new MultiPagedOneDropdownClickButtonContactInfoScraper();
			} else if (isCompanyIncludedIn(masterCompanyId, "multi.paged.one.dropdown")) {
				return new MultiPagedOneDropdownContactInfoScraper();
			} else if (isCompanyIncludedIn(masterCompanyId, "multi.paged.two.dropdowns.click.button")) {
				return new MultiPagedTwoDropdownsClickButtonContactInfoScraper();
			} else if (isCompanyIncludedIn(masterCompanyId, "one.paged.click.dropdown.options")) {
				return new OnePagedClickDropdownOptionsContactInfoScraper();
			} else if (isCompanyIncludedIn(masterCompanyId, "one.paged.click.links.to.display")) {
				return new OnePagedClickLinksToDisplayContactInfoScraper();
			} else if (isCompanyIncludedIn(masterCompanyId, "one.paged.load.more.button")) {
				return new OnePagedLoadMoreButtonContactInfoScraper();
			} else if (isCompanyIncludedIn(masterCompanyId, "one.paged.one.dropdown.click.button")) {
				return new OnePagedOneDropdownClickButtonContactInfoScraper();
			} else if (isCompanyIncludedIn(masterCompanyId, "one.paged.one.dropdown")) {
				return new OnePagedOneDropdownContactInfoScraper();
			} else if (isCompanyIncludedIn(masterCompanyId, "one.paged.split.nodes")) {
				return new OnePagedSplitNodesContactInfoScraper();
			} else if (isCompanyIncludedIn(masterCompanyId, "one.paged.two.dropdowns")) {
				return new OnePagedTwoDropdownsContactInfoScraper();
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	static boolean isCompanyIncludedIn(String masterCompanyID, String scraperNameProperty) throws Exception {
		boolean isIncluded = false;
		try {
			Properties systemProperties = new Properties();
			URL url = Thread.currentThread().getContextClassLoader().getResource("properties/companiesPerScraperProperties.properties");
			systemProperties.load(url.openStream());
			
			String companyIDsUnderCurrentScraper = systemProperties.getProperty(scraperNameProperty).trim();
			if (StringHelper.isEmpty(companyIDsUnderCurrentScraper)) {
				throw new Exception(scraperNameProperty + " property is missing. Please update properties file!");
			}
			
			isIncluded = companyIDsUnderCurrentScraper.contains(masterCompanyID);
	
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		return isIncluded;
	} 
}
