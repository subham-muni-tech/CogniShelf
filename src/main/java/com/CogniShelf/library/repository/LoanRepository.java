package com.CogniShelf.library.repository;

import com.CogniShelf.library.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    // Find all active loans for a specific member
    List<Loan> findByMemberIdAndStatus(Long memberId, String status);
}