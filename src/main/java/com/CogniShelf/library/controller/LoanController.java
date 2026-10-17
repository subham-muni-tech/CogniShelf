package com.CogniShelf.library.controller;

import com.CogniShelf.library.entity.Book;
import com.CogniShelf.library.entity.Loan;
import com.CogniShelf.library.entity.Member;
import com.CogniShelf.library.repository.BookRepository;
import com.CogniShelf.library.repository.LoanRepository;
import com.CogniShelf.library.repository.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public LoanController(LoanRepository loanRepository, BookRepository bookRepository, MemberRepository memberRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    // GET endpoint to view all loans: http://localhost:8080/api/loans
    @GetMapping
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    // POST endpoint to borrow a book: http://localhost:8080/api/loans/borrow
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody BorrowRequest request) {
        // 1. Find the book
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + request.getBookId()));

        // 2. Find the member
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found with ID: " + request.getMemberId()));

        // 3. Check if copies are available
        if (book.getAvailableCopies() <= 0) {
            return ResponseEntity.badRequest().body("Error: No available copies left for this book!");
        }

        // 4. Decrease available copies and save book
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // 5. Create and save the loan record
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setIssueDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14)); // Due in 2 weeks
        loan.setStatus("BORROWED");

        Loan savedLoan = loanRepository.save(loan);
        return ResponseEntity.ok(savedLoan);
    }

    // POST endpoint to return a book: http://localhost:8080/api/loans/return/{loanId}
    @PostMapping("/return/{loanId}")
    public ResponseEntity<?> returnBook(@PathVariable Long loanId) {
        // 1. Find the active loan by ID
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + loanId));

        // 2. Check if it's already returned
        if ("RETURNED".equals(loan.getStatus())) {
            return ResponseEntity.badRequest().body("Error: This book has already been returned!");
        }

        // 3. Update the loan record
        loan.setReturnDate(LocalDate.now());
        loan.setStatus("RETURNED");
        loanRepository.save(loan);

        // 4. Increase the book's available copies by 1
        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return ResponseEntity.ok(loan);
    }

    // Nested request helper class (No separate file needed!)
    public static class BorrowRequest {
        private Long bookId;
        private Long memberId;

        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }

        public Long getMemberId() { return memberId; }
        public void setMemberId(Long memberId) { this.memberId = memberId; }
    }
}