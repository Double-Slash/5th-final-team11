package com.doubleslash.fifth.service;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.doubleslash.fifth.dto.CommentDTO;
import com.doubleslash.fifth.dto.ContentDTO;
import com.doubleslash.fifth.dto.DetailReviewDTO;
import com.doubleslash.fifth.dto.MyReviewDTO;
import com.doubleslash.fifth.dto.ReviewDTO;
import com.doubleslash.fifth.dto.ReviewWriteDTO;
import com.doubleslash.fifth.dto.WrapperDTO;
import com.doubleslash.fifth.repository.AlcoholRepository;
import com.doubleslash.fifth.repository.CommentRepository;
import com.doubleslash.fifth.repository.DetailReviewRepository;
import com.doubleslash.fifth.repository.ReportReviewRepository;
import com.doubleslash.fifth.repository.ReportCommentRepository;
import com.doubleslash.fifth.repository.ReviewLoveRepository;
import com.doubleslash.fifth.repository.ReviewRepository;
import com.doubleslash.fifth.vo.AlcoholVO;
import com.doubleslash.fifth.vo.CommentVO;
import com.doubleslash.fifth.vo.DetailReviewVO;
import com.doubleslash.fifth.vo.ReportCommentVO;
import com.doubleslash.fifth.vo.ReportReviewVO;
import com.doubleslash.fifth.vo.ReviewLoveVO;
import com.doubleslash.fifth.vo.ReviewVO;

@Service
public class ReviewService {

	@Autowired
	ReviewRepository reviewRepository;

	@Autowired
	DetailReviewRepository detailReviewRepository;

	@Autowired
	ReviewLoveRepository reviewLoveRepository;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	ReportReviewRepository reportReviewRepository;

	@Autowired
	ReportCommentRepository reportCommentRepository;

	@Autowired
	AlcoholRepository alcoholRepository;

	// 리뷰 조회
	public Map<String, Object> getReviewList(int aid, int page, int id, HttpServletResponse response) throws IOException {

		Page<ReviewDTO> reviewDto = reviewRepository.findByAid(aid, id, PageRequest.of(page, 20, Sort.Direction.ASC, "rid"));

		for (int i = 0; i < reviewDto.getContent().size(); i++) {
			int rid = reviewDto.getContent().get(i).getRid();

			// 해당 리뷰 하트 클릭 여부 확인
			ReviewLoveVO loveClick = reviewLoveRepository.findByIdRid(id, rid);
			if (loveClick != null) {
				reviewDto.getContent().get(i).setLoveClick(true);
			} else {
				reviewDto.getContent().get(i).setLoveClick(false);
			}

			// 해당 리뷰 상세 내용
			DetailReviewDTO detailDto = detailReviewRepository.findByRid(rid);
			reviewDto.getContent().get(i).setDetail(detailDto);

			// 해당 리뷰 댓글 (최신순 3개)
			Page<CommentDTO> commentDto = commentRepository.findByRid(rid, PageRequest.of(0, 3, Sort.Direction.DESC, "cid"));
			reviewDto.getContent().get(i).setComments(commentDto.getContent());

		}

		Map<String, Object> res = new TreeMap<>();
		res.put("reviewList", reviewDto.getContent());
		res.put("totalCnt", reviewDto.getTotalElements());

		return res;
	}
	
	// 댓글 조회
	public Map<String, Object> getComment(int rid, int page) {
		
		// 오름차순
		Page<CommentDTO> commentDto = commentRepository.findByRid(rid, PageRequest.of(page, 20,  Sort.Direction.ASC, "cid"));

		Map<String, Object> res = new TreeMap<>();
		res.put("commentList", commentDto.getContent());
		res.put("totalCnt", commentDto.getTotalElements());
		
		return res;
	}

	// 리뷰 작성
	public WrapperDTO addReview(int aid, int id, ReviewWriteDTO reveiwWriteDto, HttpServletResponse response)
			throws ParseException, IOException {
		ReviewVO reviewVo = new ReviewVO();
		DetailReviewVO detailVo = new DetailReviewVO();

		AlcoholVO alcoholChk = alcoholRepository.findByAid(aid);

		if (alcoholChk == null) {
			response.sendError(404, "Alcohol Id Error");
			return null;
		}
		
		// 날짜 확인 후 리뷰 중복 조회
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String dateToStr = dateFormat.format(date);
		
		ReviewVO chk = reviewRepository.findById(id, dateToStr);

		if(chk != null) {
			response.sendError(403, "Writing Restriction");
			return null;
		}
		
		reviewVo.setId(id);
		reviewVo.setAid(aid);
		reviewVo.setStar(reveiwWriteDto.getStar());
		reviewVo.setContent(reveiwWriteDto.getContent());
		int rid = reviewRepository.save(reviewVo).getRid();
		
		if (reveiwWriteDto.getDetail() == null) {
			return new WrapperDTO("success");
		} else { // 상세 리뷰 있으면 상세 리뷰 저장
			detailVo.setRid(rid);
			detailVo.setDate(reveiwWriteDto.getDetail().getDate());
			detailVo.setPlace(reveiwWriteDto.getDetail().getPlace());
			detailVo.setDrink(reveiwWriteDto.getDetail().getDrink());
			detailVo.setHangover(reveiwWriteDto.getDetail().getHangover());
			detailVo.setPrice(reveiwWriteDto.getDetail().getPrice());
			detailVo.setPrivacy(reveiwWriteDto.getDetail().isPrivacy());

			detailReviewRepository.save(detailVo);
		}

		return new WrapperDTO("success");

	}

	// 댓글 작성
	public Map<String, Object> addComment(int id, int rid, ContentDTO content, HttpServletResponse response) throws IOException {
		if (reviewChk(rid) == 0) {
			response.sendError(404, "Review Id Error");
			return null;
		}

		CommentVO commentVo = new CommentVO();
		commentVo.setId(id);
		commentVo.setRid(rid);
		commentVo.setContent(content.getContent());
		commentRepository.save(commentVo);

		CommentDTO commentDto = commentRepository.findByCid(vo.getCid());
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
		String date = dateFormat.format(commentDto.getCommentDate());
		
		Map<String, Object> res = new TreeMap<>();
		
		res.put("rid", commentDto.getId());
		res.put("nickname", commentDto.getNickname());
		res.put("content", commentDto.getContent());
		res.put("date", date);
		
		return dto;
	}

	// 리뷰 신고
	public WrapperDTO reportReview(int id, int rid, ContentDTO content, HttpServletResponse response)
			throws IOException {
		if (reviewChk(rid) == 0) {
			response.sendError(404, "Review Id Error");
			return null;
		}

		ReportReviewVO reportVo = new ReportReviewVO();
		reportVo.setRid(rid);
		reportVo.setId(id);
		reportVo.setContent(content.getContent());
		reportReviewRepository.save(reportVo);
		reviewRepository.updateReport(rid);

		WrapperDTO dto = new WrapperDTO("success");

		return dto;
	}

	// 댓글 신고
	public WrapperDTO reportComment(int id, int cid, ContentDTO content, HttpServletResponse response)
			throws IOException {
		Optional<CommentVO> commentChk = commentRepository.findById(cid);

		if (commentChk.isPresent() == false) {
			response.sendError(404, "Comment Id Error");
			return null;
		}

		ReportCommentVO reportVo = new ReportCommentVO();
		reportVo.setCid(cid);
		reportVo.setId(id);
		reportVo.setContent(content.getContent());
		reportCommentRepository.save(reportVo);
		commentRepository.updateReport(cid);

		WrapperDTO dto = new WrapperDTO("success");

		return dto;
	}

	// 리뷰 좋아요
	public Map<String, Object>  reviewLove(int id, int rid, HttpServletResponse response) throws SQLIntegrityConstraintViolationException, IOException{

		if(reviewLoveRepository.insert(id, rid)==1) {
			reviewRepository.updateLove(rid);
		}
		
		Map<String, Object> res = new TreeMap<>();
		res.put("love", true);
		res.put("loveTotalCnt", reviewRepository.findById(rid).get().getLove());

		return res;
	}

	// 리뷰 좋아요 취소
	public Map<String, Object> reviewLoveCancle(int id, int rid, HttpServletResponse response) throws IOException {
		
		if(reviewLoveRepository.delete(id, rid)==1) {
			reviewRepository.updateLoveCancle(rid);
		}

		Map<String, Object> res = new TreeMap<>();
		res.put("love", false);
		res.put("loveTotalCnt", reviewRepository.findById(rid).get().getLove());

		return res;
	}

	// 리뷰 id 확인
	public int reviewChk(int rid) {
		int result = 0;

		if(reviewRepository.findById(rid).isPresent() == true) {
			result = 1;
		}

		return result;
	}

	//내 기록 조회
	public Map<String, Object> getMyReviewList(int id, String sort, String sortOption, int page) {
		if(!sort.equals("abv")) sort = "create_time";
		Page<MyReviewDTO> myReviewTemp;
		if(sort.equals("create_time")) {
			myReviewTemp = reviewRepository.getMyReviewListOrderByLatest(id, PageRequest.of(page, 20, dirOption(sortOption), sort));
		}else {
			myReviewTemp = reviewRepository.getMyReviewListOrderByAbv(id, PageRequest.of(page, 20));
		}
		
		List<MyReviewDTO> dto = new ArrayList<MyReviewDTO>();
		
		for(MyReviewDTO m : myReviewTemp) {
			DetailReviewDTO detailReview = detailReviewRepository.findByRid(m.getRid());
			dto.add(new MyReviewDTO(m.getRid(), m.getAid(), m.getName(), m.getStar(), m.getThumbnail(), m.getContent(), detailReview));
		}
		
		Map<String, Object> res = new TreeMap<>();
		res.put("reviewList", dto);
		res.put("totalCnt", myReviewTemp.getTotalElements());
		
		return res;
	}

	
	//내 기록 수정
	public WrapperDTO updateMyReview(ReviewWriteDTO requestBody, int id, int rid) {
		ReviewVO reviewVo = new ReviewVO();
		DetailReviewVO detailVo = new DetailReviewVO();
		
		reviewRepository.updateReview(rid, requestBody.getStar(), requestBody.getContent());
		
		if (requestBody.getDetail() == null) {
			return new WrapperDTO("success");
		} else { // 상세 리뷰 있으면 상세 리뷰 저장
			detailVo.setRid(rid);
			detailVo.setDate(requestBody.getDetail().getDate());
			detailVo.setPlace(requestBody.getDetail().getPlace());
			detailVo.setDrink(requestBody.getDetail().getDrink());
			detailVo.setHangover(requestBody.getDetail().getHangover());
			detailVo.setPrice(requestBody.getDetail().getPrice());
			detailVo.setPrivacy(requestBody.getDetail().isPrivacy());

			detailReviewRepository.save(detailVo);
		}

		return new WrapperDTO("success");
		
	}
	
	
	//내 기록 삭제
	public void deleteMyReview(int rid) {
		reviewRepository.deleteById(rid);
	}
	
	//정렬 기준을 동적으로 설정
	private Sort sortOption(Direction direction, String property) {
		List<Order> orders = new ArrayList<Sort.Order>();
		orders.add(new Order(direction, property));
		return Sort.by(orders);
	}
	
	//정렬 기준별 오름차순, 내림차순 구분
	private Direction dirOption(String sortOption) {
		if(sortOption.equals("desc")) return Sort.Direction.DESC;
		return Sort.Direction.ASC;
	}

}
