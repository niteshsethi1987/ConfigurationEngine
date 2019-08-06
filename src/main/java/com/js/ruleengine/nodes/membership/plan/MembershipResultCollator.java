package com.js.ruleengine.nodes.membership.plan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.js.ruleengine.constants.IODataConstants;
import com.js.ruleengine.domains.JsMembershipCartSelected;
import com.js.ruleengine.domains.JsMembershipDiscount;
import com.js.ruleengine.domains.JsMembershipPlan;

import lombok.extern.slf4j.Slf4j;
import orchestrator.IOrchestratorContext;
import orchestrator.ResponseBodyHeader;
import orchestrator.WorkNode;

@Slf4j
@Service
public class MembershipResultCollator extends WorkNode {

	@SuppressWarnings({ "unchecked" })
	@Override
	public boolean execute(IOrchestratorContext context) throws Exception {

		log.debug("====== executing MembershipResultCollator =====");
		Map<String, Object> membershipPlanBenefit = (Map<String, Object>) context
				.getIOData(IODataConstants.MEMBERSHIP_PLAN_BENEFIT);
		Integer profileMTongue = (Integer) context.getIOData(IODataConstants.MOTHER_TONGUE);
		Map<String, Object> discountt = (Map<String, Object>) context
				.getIOData(IODataConstants.MEMBERSHIP_PROFILE_DISCOUNT);
		Map<String, Object> preSelectedCartResponse = (Map<String, Object>) context
				.getIOData(IODataConstants.PROFILE_SELECTED_CART);
		Boolean isLoggedIn = (Boolean) context.getIOData(IODataConstants.IS_LOGGED_IN);
		Integer currencyId =  (Integer) context.getIOData(IODataConstants.CURRENCY_ID);
		Map<Integer, JsMembershipPlan> membershipPlanItem = (Map<Integer, JsMembershipPlan>) membershipPlanBenefit
				.get("items");
		List<Map<String, Object>> items = new ArrayList<>();
		if (isLoggedIn == false) {
			items = populatePlanBenefitForLoggedOutUser(membershipPlanItem);
		} else {
			Map<Integer, JsMembershipCartSelected> preSelectedCart = (Map<Integer, JsMembershipCartSelected>) preSelectedCartResponse
					.get("items");
			Set<Integer> preSelectedCarItemIds = preSelectedCart.keySet();
			Map<Integer, JsMembershipDiscount> discPerService = (Map<Integer, JsMembershipDiscount>) discountt
					.get("items");
			items = getItems(membershipPlanItem, profileMTongue, currencyId, discPerService, preSelectedCarItemIds);
		}
		Map<String, Object> responseData = new LinkedHashMap<>();
		responseData.put("count", items.size());
		responseData.put("items", items);
		log.info("Data processed successfully");
		log.debug("Response Data Items {} ", items);
		context.setResponseBodyHeader(new ResponseBodyHeader(200, null));
		context.setResponseBodyData(responseData);
		return true;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> populatePlanBenefitForLoggedOutUser(Map<Integer, JsMembershipPlan> remoteItem) {
		List<Map<String, Object>> response = new ArrayList<>();
		if (null == remoteItem) {
			return response;
		}

		remoteItem.forEach((secid, jsmp) -> {
			Map<String, Object> res = new LinkedHashMap<>();
			List<Map<String, Object>> remotePlanList = (List<Map<String, Object>>) jsmp.getField("membershipPlans");
			List<Map<String, Object>> remoteAddOnList = (List<Map<String, Object>>) jsmp.getField("addOns");
			res.put("secid", secid);
			res.put("plans", populateServiceForLoggedOutUser(remotePlanList));
			res.put("benefits", jsmp.getField("benefits"));
			res.put("addOns", populateServiceForLoggedOutUser(remoteAddOnList));
			response.add(res);
		});

		return response;
	}

	private List<Map<String, Object>> populateServiceForLoggedOutUser(List<Map<String, Object>> remotePlan) {
		List<Map<String, Object>> planListLoggedOutUser = new ArrayList<>();
		if (null == remotePlan) {
			return planListLoggedOutUser;
		}
		remotePlan.forEach(p -> {
			Map<String, Object> planForloggedOut = new LinkedHashMap<>();
			planForloggedOut.put("name", p.get("name"));
			planForloggedOut.put("benefits", p.get("benefits"));
			planListLoggedOutUser.add(planForloggedOut);
		});
		return planListLoggedOutUser;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getItems(Map<Integer, JsMembershipPlan> remoteItem, Integer profileMTongue,
			Integer currencyId, Map<Integer, JsMembershipDiscount> discount, Set<Integer> preSelectedCarItemIds) {
		List<Map<String, Object>> response = new ArrayList<>();
		if (null == remoteItem) {
			return response;
		}

		remoteItem.forEach((secid, jsmp) -> {
			Map<String, Object> res = new LinkedHashMap<>();
			List<Map<String, Object>> remotePlanList = (List<Map<String, Object>>) jsmp.getField("membershipPlans");
			List<Map<String, Object>> remoteAddOnList = (List<Map<String, Object>>) jsmp.getField("addOns");
			if (null == remoteAddOnList && null == remotePlanList)
				return;
			Set<Integer> mTongueListNotEligibleForDefaultService = getMToungeListNotEligibleForDefaultService(
					remotePlanList, remoteAddOnList);

			List<Map<String, Object>> planListForGivenRegion = getPlanListForGivenRegion(
					mTongueListNotEligibleForDefaultService, profileMTongue, currencyId, discount, remotePlanList,
					preSelectedCarItemIds);

			List<Map<String, Object>> addOnListForGivenRegion = getPlanListForGivenRegion(
					mTongueListNotEligibleForDefaultService, profileMTongue, currencyId, discount, remoteAddOnList,
					preSelectedCarItemIds);

			res.put("secid", secid);
			res.put("plans", planListForGivenRegion);
			res.put("benefits", jsmp.getField("benefits"));
			res.put("addOns", addOnListForGivenRegion);
			response.add(res);
		});

		return response;
	}

	private Set<Integer> getMToungeListNotEligibleForDefaultService(List<Map<String, Object>> remotePlanList,
			List<Map<String, Object>> remoteAddOnList) {
		Set<Integer> mTongueListNotEligibleForDefaultService = new HashSet<>();
		mTongueListNotEligibleForDefaultService.addAll(getMToungeListFromPlan(remotePlanList));
		mTongueListNotEligibleForDefaultService.addAll(getMToungeListFromPlan(remoteAddOnList));
		return mTongueListNotEligibleForDefaultService;
	}

	@SuppressWarnings("unchecked")
	private Set<Integer> getMToungeListFromPlan(List<Map<String, Object>> remotePlan) {
		Set<Integer> mTongueList = new HashSet<>();
		if (null == remotePlan) {
			return mTongueList;
		}
		remotePlan.forEach(rP -> {
			List<Map<String, Object>> service = (List<Map<String, Object>>) rP.get("service");
			service.forEach(s -> {
				List<Map<String, Object>> regionList = (List<Map<String, Object>>) s.get("mtList");
				if (null == regionList) {
					return;
				}
				regionList.forEach(r -> {
					Integer id = (Integer) r.get("id");
					mTongueList.add(id);
				});
			});
		});
		return mTongueList;
	}

	private List<Map<String, Object>> getPlanListForGivenRegion(Set<Integer> mTongueListNotEligibleForDefaultService,
			Integer profileMTongue, Integer currencyId, Map<Integer, JsMembershipDiscount> discount,
			List<Map<String, Object>> remotePlanList, Set<Integer> preSelectedCarItemIds) {
		List<Map<String, Object>> planListForGivenRegion = new ArrayList<>();

		if (null == remotePlanList) {
			return planListForGivenRegion;
		}

		remotePlanList.forEach(p -> {
			List<Map<String, Object>> serviceListForCurrentPlan = getServicesForCurrentPlanValidInGivenRegion(p,
					mTongueListNotEligibleForDefaultService, profileMTongue, currencyId, discount,
					preSelectedCarItemIds);
			if (null == serviceListForCurrentPlan) {
				return;
			}
			Map<String, Object> planForGivenRegion = new LinkedHashMap<>();
			planForGivenRegion.put("name", p.get("name"));
			// need to calculate popular service id again
			planForGivenRegion.put("popSerid", getMostPopularServiceId(serviceListForCurrentPlan));
			planForGivenRegion.put("benefits", p.get("benefits"));
			planForGivenRegion.put("ser", serviceListForCurrentPlan);
			planListForGivenRegion.add(planForGivenRegion);
		});

		return planListForGivenRegion;
	}

	private Integer getMostPopularServiceId(List<Map<String, Object>> serviceListForCurrentPlan) {

		int max = -1, popular = -1;

		for (Map<String, Object> service : serviceListForCurrentPlan) {
			if ((Integer) service.get("serPop") > max) {
				max = (Integer) service.get("serPop");
				popular = (Integer) service.get("id");
			}
		}
		return popular;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getServicesForCurrentPlanValidInGivenRegion(Map<String, Object> p,
			Set<Integer> mTongueListNotEligibleForDefaultService, Integer profileMTongue, Integer currencyId,
			Map<Integer, JsMembershipDiscount> discount, Set<Integer> preSelectedCarItemIds) {
		List<Map<String, Object>> service = (List<Map<String, Object>>) p.get("service");
		if (null == service) {
			return null;
		}

		List<Map<String, Object>> serviceListForCurrentPlan = new ArrayList<>();
		service.forEach(s -> {
			if (isSAvailableForRegion(s, mTongueListNotEligibleForDefaultService, profileMTongue)) {
				// calculate discount for this s and
				Map<String, Object> priceForGivenGeography = getPrice(s, currencyId, discount);
				Map<String, Object> ser = new LinkedHashMap<>();
				ser.put("id", s.get("id"));
				ser.put("isCart", preSelectedCarItemIds.contains(s.get("id")));
				ser.put("code", s.get("code"));
				ser.put("name", s.get("name"));
				ser.put("dur", s.get("duration"));
				ser.put("cont", s.get("contacts"));
				ser.put("mess", s.get("messages"));
				ser.put("serPop", s.get("popularityIndex"));
				ser.put("mt", profileMTongue);
				ser.put("price", priceForGivenGeography);
				serviceListForCurrentPlan.add(ser);
			}
		});

		return serviceListForCurrentPlan;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getPrice(Map<String, Object> s, Integer currencyId,
			Map<Integer, JsMembershipDiscount> remDiscount) {

		List<Map<String, Object>> prices = (List<Map<String, Object>>) s.get("prices");
		if (null == prices) {
			return null;
		}
		JsMembershipDiscount remoteDiscountEntity = (JsMembershipDiscount) remDiscount.get((Integer) (s.get("id")));

		Map<String, Object> priceForGivenGeo = new LinkedHashMap<>();
		prices.forEach(pr -> {
			if ((Integer) pr.get("id") == currencyId) {
				priceForGivenGeo.put("id", currencyId);
				priceForGivenGeo.put("iosAct", pr.get("iosPrice"));
				priceForGivenGeo.put("iosEff", getDiscountedPrice((Double) pr.get("iosPrice"), remoteDiscountEntity));

				priceForGivenGeo.put("andAct", pr.get("androidPrice"));
				priceForGivenGeo.put("andEff",
						getDiscountedPrice((Double) pr.get("androidPrice"), remoteDiscountEntity));

				priceForGivenGeo.put("mSiteAct", pr.get("msitePrice"));
				priceForGivenGeo.put("mSiteEff",
						getDiscountedPrice((Double) pr.get("msitePrice"), remoteDiscountEntity));

				priceForGivenGeo.put("deskWebAct", pr.get("deskWebPrice"));
				priceForGivenGeo.put("deskWebEff",
						getDiscountedPrice((Double) pr.get("deskWebPrice"), remoteDiscountEntity));

			}
		});

		return priceForGivenGeo;
	}

	private Object getDiscountedPrice(Double actualPrice, JsMembershipDiscount remDiscountEntity) {
		Double discount = 0.0;
		if (!(remDiscountEntity == null)) {
			discount = (Double) remDiscountEntity.getField("dis");
		}
		return (100 - discount) * actualPrice / 100;
	}

	private boolean isSAvailableForRegion(Map<String, Object> s, Set<Integer> mTongueListNotEligibleForDefaultService,
			Integer ProfileMt) {
		if (mTongueListNotEligibleForDefaultService.contains(ProfileMt)) {
			return getIfMatch(s, ProfileMt);
		}
		return getIfMatch(s, 0);
	}

	@SuppressWarnings("unchecked")
	private boolean getIfMatch(Map<String, Object> s, Integer profileMt) {
		List<Map<String, Object>> region = (List<Map<String, Object>>) s.get("mtList");
		if (null == region) {
			return false;
		}
		return region.stream().anyMatch(r -> (Integer) r.get("id") == profileMt);
	}

}
