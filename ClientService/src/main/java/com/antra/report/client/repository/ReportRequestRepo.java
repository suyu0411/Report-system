package com.antra.report.client.repository;

import com.antra.report.client.entity.ReportRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ReportRequestRepo extends JpaRepository<ReportRequestEntity, String> {
    ReportRequestEntity findDistinctBySubmitter(String submitter);
}
