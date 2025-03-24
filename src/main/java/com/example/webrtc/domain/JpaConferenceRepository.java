package com.example.webrtc.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaConferenceRepository extends JpaRepository<Conference, Long> {
}
