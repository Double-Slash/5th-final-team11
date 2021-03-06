package com.doubleslash.fifth.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.doubleslash.fifth.dto.LoveClickDTO;
import com.doubleslash.fifth.dto.WrapperDTO;
import com.doubleslash.fifth.service.AlcoholService;
import com.doubleslash.fifth.service.AuthService;
import com.doubleslash.fifth.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Alcohol", description = "주류 상세 조회 API" )
@Controller
@RequestMapping(value = "/alcohol")
public class AlcoholController {
	
	@Autowired
	AuthService authService;
	
	@Autowired
	AlcoholService alcoholService;

	@Autowired
	UserService userService;
	
	@ApiOperation(value = "주류 세부 사항 조회", notes="주류 공통 속성 \n: aid(주류id), name(주류명), category(카테고리), image(이미지경로), lowestPrice(최저가격), highest(최고가격), ml(용량), abv(도수), description(설명), kind(종류, list 타입), starAvg(별점평균), starCnt(별점수), userDrink(사용자주량), smiliar(비슷한 술 정보)"
			+ "\n 주류별 추가 속성 \n: 양주 - flavors(맛, list 타입), \n  맥주 - areas(지역, list 타입), \n  와인 - country(국가), area(지역), flavor(맛, int 타입(1~5로 구분)), body(바디감, int 타입(1~5로 구분))"
			+ "\n :: 추천사항 정보인 종류, 맛, 지역은 list로 제공")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Alcohol Information Get Succues "),		
		@ApiResponse(code = 400, message = "Alcohol Id Error"),
	})
	@ApiImplicitParam(name = "Authorization", value = "idToken", required = false, paramType = "header")
	@GetMapping(value = "/{aid}", produces = "application/json; charset=utf8")
	@ResponseBody
	public String detail(@PathVariable("aid") int aid, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String uid = authService.verifyToken(request);
		int id;
		
		if(uid == null) {
			id = -1;
		}else {
			id = userService.getId(uid);
		}

		Map<String, Object> map = new HashMap<String, Object>();
	
		String category = alcoholService.getCategory(aid);
		
		String result ="";

		if(category == null) {
			response.sendError(400, "Alcohol Id Error");
		}else {
			response.setStatus(200);
			if(category.equals("양주")) {
				map = alcoholService.getLiquor(id, aid);
			}else if(category.equals("세계맥주")) {
				map = alcoholService.getBeer(id, aid);
			}else if(category.equals("와인")) {
				map = alcoholService.getWine(id, aid);
			}
			JSONObject jsonObject = new JSONObject(map);
			result = jsonObject.toJSONString();
		}
		
		return result;
	}
	
	@ApiOperation(value = "주류 찜하기", notes = "true : 찜하기, false : 찜하기 취소")
	@ApiResponses({
		@ApiResponse(code = 200, message = "Success"),
		@ApiResponse(code = 404, message = "Alcohol Id Error")
	})
	@PutMapping(value = "/{aid}/love")
	@ResponseBody
	public Map<String, Object> alcoholLove(@PathVariable int aid, @RequestBody LoveClickDTO loveClick, HttpServletRequest request) throws Exception {
		String uid = authService.verifyToken(request);
		int id = userService.getId(uid);

		Map<String, Object> res = new TreeMap<>();
		
		if(loveClick.getLoveClick() == true) {
			res = alcoholService.alcoholLove(id, aid);
		} else if(loveClick.getLoveClick() == false) {
			res = alcoholService.alcoholLoveCancle(id, aid);
		}
		
		return res;

	}
}
