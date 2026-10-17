package com.CogniShelf.library.controller;

import com.CogniShelf.library.entity.Member;
import com.CogniShelf.library.repository.MemberRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberRepository memberRepository;

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // GET endpoint to view all members: http://localhost:8080/api/members
    @GetMapping
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    // POST endpoint to register a new member: http://localhost:8080/api/members
    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberRepository.save(member);
    }
}