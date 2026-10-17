package com.CogniShelf.library.repository;

import com.CogniShelf.library.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // Spring Data JPA automatically handles basic CRUD and we can add custom finders later!
}