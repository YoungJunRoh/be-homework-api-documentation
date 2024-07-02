package com.springboot.homework;


import com.jayway.jsonpath.JsonPath;
import com.springboot.member.controller.MemberController;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.springboot.stamp.Stamp;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.List;

import static com.springboot.util.ApiDocumentUtils.getRequestPreProcessor;
import static com.springboot.util.ApiDocumentUtils.getResponsePreProcessor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class MemberControllerDocumentationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberMapper mapper;

    @Autowired
    private Gson gson;

    @Test
    public void getMemberTest() throws Exception {
        // TODO 여기에 MemberController의 getMember() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        long memberId = 1L;
        MemberDto.Response response = new MemberDto.Response(1L,
                "shdudwns59@naver.com",
                "노영준",
                "010-8859-5755",
                Member.MemberStatus.MEMBER_ACTIVE,
                new Stamp());

        given(memberService.findMember(Mockito.anyLong())).willReturn(new Member());
        given(mapper.memberToMemberResponse(Mockito.any(Member.class))).willReturn(response);

//        URI uri = UriComponentsBuilder.newInstance().path("/v11/members/{memberId}").buildAndExpand(memberId).toUri();

        // when
        ResultActions actions = mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get("/v11/members/{member-id}", memberId)
                        .accept(MediaType.APPLICATION_JSON)
        );
        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("shdudwns59@naver.com"))
                .andExpect(jsonPath("$.data.name").value("노영준"))
                .andDo(document(
                        "get-member",
                        preprocessRequest(),
                        preprocessResponse(),
                        pathParameters(
                                parameterWithName("member-id").description("회원 식별자")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름"),
                                        fieldWithPath("data.phone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                                        fieldWithPath("data.memberStatus").type(JsonFieldType.STRING)
                                                .description("회원 상태 : 활동 중 / 휴면 상태 / 탈퇴 상태"),
                                        fieldWithPath("data.stamp").type(JsonFieldType.NUMBER).description("스탬프 갯수")
                                )
                        )
                ));

    }

    @Test
    public void getMembersTest() throws Exception {
        // TODO 여기에 MemberController의 getMembers() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        Member member1 = new Member
                ("shdudwns59@gmail.com", "노영준", "010-8859-5755");
        member1.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
        member1.setStamp(new Stamp());
        Member member2 = new Member
                ("shdudwns@gmail.com", "노영천", "010-8869-5755");
        member2.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
        member2.setStamp(new Stamp());

        Page<Member> pageMembers = new PageImpl<>(List.of(member1, member2),
                PageRequest.of(0, 10, Sort.by("memberId").descending()), 2);

        List<MemberDto.Response> responses = List.of(
                new MemberDto.Response(1L,
                        "shdudwns59@gmail.com",
                        "노영준",
                        "010-8859-5755",
                        Member.MemberStatus.MEMBER_ACTIVE,
                        member1.getStamp()),
                new MemberDto.Response(2L,
                        "shdudwns@gmail.com",
                        "노영찬",
                        "010-8869-5755",
                        Member.MemberStatus.MEMBER_ACTIVE,
                        member1.getStamp())
        );

        given(memberService.findMembers(Mockito.anyInt(), Mockito.anyInt())).willReturn(pageMembers);
        given(mapper.membersToMemberResponses(Mockito.anyList())).willReturn(responses);

        String page = "1";
        String size = "10";
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", page);
        queryParams.add("size", size);

//        URI uri = UriComponentsBuilder.newInstance().path("/v11/members").build().toUri();

        ResultActions actions = mockMvc.perform(
                RestDocumentationRequestBuilders
                        .get("/v11/members")
                        .params(queryParams)
                        .accept(MediaType.APPLICATION_JSON)
        );

        MvcResult result = actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].email").value("shdudwns59@gmail.com"))
                .andExpect(jsonPath("$.data[1].email").value("shdudwns@gmail.com"))
                .andDo(document(
                                "get-members",
                        preprocessRequest(),
                        preprocessResponse(),
                                requestParameters(
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("페이지 크기")
                                ),
                                responseFields(
                                        List.of(
                                                fieldWithPath("data").type(JsonFieldType.ARRAY).description("결과 데이터"),
                                                fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                                fieldWithPath("data[].email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("이름"),
                                                fieldWithPath("data[].phone").type(JsonFieldType.STRING).description("휴대폰 번호"),
                                                fieldWithPath("data[].memberStatus").type(JsonFieldType.STRING)
                                                        .description("회원 상태 : 활동 중 / 휴면 상태 / 탈퇴 상태"),
                                                fieldWithPath("data[].stamp").type(JsonFieldType.NUMBER).description("스탬프 갯수"),
                                                fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                                fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                                fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                                fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 개수"),
                                                fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수")
                                        )
                                )
                        )
                )
                .andReturn();

        List list = JsonPath.parse(result.getResponse().getContentAsString()).read("$.data");

        assertThat(list.size(), is(2));


    }

    @Test
    public void deleteMemberTest() throws Exception {
        // TODO 여기에 MemberController의 deleteMember() 핸들러 메서드 API 스펙 정보를 포함하는 테스트 케이스를 작성 하세요.
        long memberId = 1L;
//        URI uri = UriComponentsBuilder.newInstance().path("/v11/members/{member-id}").buildAndExpand(memberId).toUri();
        doNothing().when(memberService).deleteMember(Mockito.anyLong());
        //when
        ResultActions actions = mockMvc.perform(
               delete("/v11/members/{member-id}", memberId)
        );

        // then
        actions.andExpect(status().isNoContent())
                .andDo(document("delete-member",
                        preprocessRequest(),
                        preprocessResponse(),
                        pathParameters(
                                parameterWithName("member-id").description("회원식별자")
                        )
                ));


    }
}
