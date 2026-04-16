package com.wanted.naeil.domain.user.service;


import com.wanted.naeil.domain.user.dto.LoginUserDTO;
import com.wanted.naeil.domain.user.dto.SignupDTO;
import com.wanted.naeil.domain.user.entity.User;
import com.wanted.naeil.domain.user.repository.UserRepository;
import com.wanted.naeil.global.common.exception.CustomException;
import com.wanted.naeil.global.common.exception.ErrorCode;
import com.wanted.naeil.global.util.file.LocalFileService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final ModelMapper modelMapper;
//    private final ResourceLoader resourceLoader;
private final LocalFileService localFileService;

    @Transactional
    public Long regist(SignupDTO signupDTO) {

        // 비밀번호 일치 여부
        if (!signupDTO.getPassword().equals(signupDTO.getPasswordConfirm())) {
            // ErrorCode의 INVALID_PASSWORD 활용
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }


        // 중복 체크 - null 반환 대신 ErrorCode로 예외를 던진다
        if (userRepository.existsByUsername(signupDTO.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME); // 유저 네임 중복
        }
        if (userRepository.existsByEmail(signupDTO.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL); // 이메일 중복
        }
        if (userRepository.existsByNickname(signupDTO.getNickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME); // 닉네임 중복
        }

      //파일 업로드 로직을 한 줄로 대체
        String profileImgPath = null;
        MultipartFile profileImg = signupDTO.getProfileImg();

        if (profileImg != null && !profileImg.isEmpty()) {
            validateProfileImg(profileImg); // 기존 검증 로직은 유지하는 게 안전합니다.

            // 팀원분이 만든 유틸 사용: (파일, 저장할폴더명)
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



        //4. DB 저장
        return userRepository.save(user).getId();
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
            // ErrorCode에 적절한 게 없다면 INVALID_INPUT 활용
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        // 2. 용량 제한 체크: 5MB (5 * 1024 * 1024 bytes)
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            // 용량 초과 시 예외 발생
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR); // 상황에 맞는 ErrorCode 선택
        }
    }

    public LoginUserDTO findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new LoginUserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getName(),
                        user.getPassword(),
                        user.getRole(),
                        user.getStatus(),
                        user.getNickname(),
                        user.getProfileImg()

                ))
                .orElse(null);
    }


}