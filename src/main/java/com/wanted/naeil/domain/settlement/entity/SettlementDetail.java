package com.wanted.naeil.domain.settlement.entity;

import com.wanted.naeil.domain.course.entity.Course;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "settlement_detail")
@Getter
@NoArgsConstructor
public class SettlementDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "sale_count", nullable = false)
    private int saleCount;

    @Column(name = "total_sales_amount", nullable = false)
    private int totalSalesAmount;

    @Column(name = "final_amount", nullable = false)
    private int finalAmount;

    @Builder
    public SettlementDetail(Course course, int saleCount, int totalSalesAmount, int finalAmount) {
        this.course = course;
        this.saleCount = saleCount;
        this.totalSalesAmount = totalSalesAmount;
        this.finalAmount = finalAmount;
    }

    public void assignSettlement(Settlement settlement) {
        this.settlement = settlement;
    }
}
