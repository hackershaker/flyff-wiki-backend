package io.github.flyff_wiki.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.flyff_wiki.domain.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

}
