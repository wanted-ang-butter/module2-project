package com.wanted.naeil.domain.user.service;


import com.wanted.naeil.domain.payment.entity.Credit;
import com.wanted.naeil.domain.payment.repository.CreditRepository;
import com.wanted.naeil.domain.user.dto.request.FindIdRequest;
import com.wanted.naeil.domain.user.dto.request.FindPasswordRequest;
import com.wanted.naeil.domain.user.dto.response.FindIdResponse;
import com.wanted.naeil.domain.user.dto.response.FindPasswordResponse;
import com.wanted.naeil.domain.user.dto.response.LoginUserDTO;
import com.wanted.naeil.domain.user.dto.request.SignupDTO;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import com.wanted.naeil.global.util.file.LocalFileService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final ModelMapper modelMapper;
    private final CreditRepository creditRepository;
//    private final ResourceLoader resourceLoader;
private final LocalFileService localFileService;

    // 회원가입
    @Transactional
    public Long regist(SignupDTO signupDTO) {

        // 비밀번호 일치 여부
        if (!signupDTO.getPassword().equals(signupDTO.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 중복 정보 검증
        if (userRepository.existsByUsername(signupDTO.getUsername())) {
            throw new DuplicateKeyException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(signupDTO.getEmail())) {
            throw new DuplicateKeyException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByNickname(signupDTO.getNickname())) {
            throw new DuplicateKeyException("이미 사용 중인 닉네임입니다.");
        }

      //파일 업로드 로직
        String profileImgPath = null;
        MultipartFile profileImg = signupDTO.getProfileImg();

        if (profileImg != null && !profileImg.isEmpty()) {
            validateProfileImg(profileImg); // 기존 검증 로직은 유지하는 게 안전합니다.

            // 유틸 사용: (파일, 저장할폴더명)
            // directory 매개변수에 "profiles"를 전달하면 실제 경로는 /upload/profiles/... 가 됩니다.
            profileImgPath = localFileService.uploadSingleFile(profileImg, "profiles");
        }

        // 3. 빌더
        User user = User.builder()
                .username(signupDTO.getUsername())
                .password(encoder.encode(signupDTO.getPassword()))
                .name(signupDTO.getName())
                .nickname(signupDTO.getNickname())
                .email(signupDTO.getEmail())
                .phone(signupDTO.getPhone())
                .birthDate(signupDTO.getBirthDate())
                .profileImg(profileImgPath)
                .build();


        // 4. DB 저장
        User savedUser = userRepository.save(user);


        // 5. 크레딧 0으로 초기화
        Credit credit = Credit.builder()
                .user(savedUser)
                .balance(0)
                .build();
        creditRepository.save(credit);

        return savedUser.getId();
    }


   // 프로필 이미지 유효성 검증 로직
    private void validateProfileImg(MultipartFile file) {
        // 이미지가 전송되지 않은 경우(선택 사항)는 통과
        if (file == null || file.isEmpty()) {
            return;
        }

        // 1. 파일 형식(MIME Type) 체크: jpg, jpeg, png 만 허용
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
            throw new IllegalArgumentException("jpg, jpeg, png 형식만 가능합니다.");
        }

        // 2. 용량 제한 체크: 5MB (5 * 1024 * 1024 bytes)
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            // 용량 초과 시 예외 발생
            throw new IllegalArgumentException("이미지 크기는 5MB 이하여야 합니다.");
        }
    }

    public LoginUserDTO findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new LoginUserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getName(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getRole(),
                        user.getStatus(),
                        user.getNickname(),
                        user.getProfileImg()

                ))
                .orElse(null);
    }

    // 이메일, 전화번호로 아이디 찾기
    public FindIdResponse findUsernameByEmailAndPhone(FindIdRequest request) {
        User user = userRepository.findByEmailAndPhone(request.getEmail(), request.getPhone())
                .orElseThrow(() -> new NoSuchElementException("일치하는 회원 정보가 없습니다."));
        return FindIdResponse.builder()
                .username(user.getUsername())
                .build();
    }

    // 아이디, 전화번호 일치하면 임시 비밀번호 발급
    @Transactional
    public FindPasswordResponse resetPassword(FindPasswordRequest request) {
        User user = userRepository.findByUsernameAndPhone(request.getUsername(), request.getPhone())
                .orElseThrow(() -> new NoSuchElementException("일치하는 회원 정보가 없습니다."));

        // 임시 비밀번호 생성 (8자리 랜덤)
        String tempPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        // 해싱 후 DB 업데이트
        user.password(encoder.encode(tempPassword));

        return FindPasswordResponse.builder()
                .tempPassword(tempPassword)
                .build();
    }


}