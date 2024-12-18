# java-convenience-store-precourse

---

## 기능 요구 사항

### 결제 시스템

- **최종 결제 금액 계산**: 상품 가격과 수량을 기반으로 최종 결제 금액을 계산합니다.
    - 상품별 가격에 수량을 곱한 총구매액에 프로모션과 멤버십 할인을 적용하여 계산합니다.
    - 구매 내역과 산출 금액 정보를 영수증에 출력합니다.
- **추가 구매 여부**: 영수증 출력 후 추가 구매 진행 여부를 선택할 수 있습니다.
- **입력 오류 처리**: 잘못된 값 입력 시 `IllegalArgumentException` 발생 및 "[ERROR]"로 시작하는 메시지 출력 후 다시 입력 받습니다.
    - `IllegalArgumentException`과 `IllegalStateException` 등의 예외를 처리합니다.

### 재고 관리

- **재고 수량 확인**: 결제 가능 여부를 재고 수량을 통해 확인합니다.
- **재고 차감**: 상품 구매 시 해당 수량만큼 재고에서 차감하여 최신 상태 유지합니다.
- **정확한 재고 정보 제공**: 다음 고객 구매 시 정확한 재고 정보를 제공할 수 있도록 업데이트합니다.

### 프로모션 할인

- **프로모션 기간 확인**: 오늘 날짜가 프로모션 기간 내에 포함될 경우에만 할인을 적용합니다.
- **프로모션 형태**: `N+1` 형태의 할인으로 `1+1` 또는 `2+1` 프로모션이 지정된 상품에 적용됩니다.
    - 동일 상품에 여러 프로모션이 중복 적용되지 않습니다.
    - 프로모션은 프로모션 재고 내에서만 적용 가능하며 부족 시 일반 재고 사용합니다.
- **프로모션 혜택 안내**: 필요한 수량을 추가로 가져오면 혜택을 받을 수 있음을 안내합니다.
    - 프로모션 재고가 부족하여 일부 수량을 정가로 결제해야 할 경우 이를 안내합니다.

### 멤버십 할인

- **할인율**: 멤버십 회원은 프로모션 미적용 금액의 30%를 할인받습니다.
- **적용 방식**: 프로모션 적용 후 남은 금액에 대해 멤버십 할인을 적용하며 최대 할인 한도는 8,000원입니다.

### 영수증 출력

- **구매 내역**: 구매한 상품명, 수량, 가격을 포함합니다.
- **증정 상품 내역**: 프로모션에 따라 무료로 제공된 상품 목록을 포함합니다.
- **금액 정보**:
    - **총구매액**: 구매한 상품의 총 수량과 총 금액입니다.
    - **행사할인**: 프로모션으로 할인된 금액입니다.
    - **멤버십할인**: 멤버십으로 추가 할인된 금액입니다.
    - **내실돈**: 최종 결제 금액입니다.
- **영수증 레이아웃**: 구성 요소를 정렬하여 고객이 쉽게 확인할 수 있도록 합니다.

----

## 기능 목록 정리

- [x] **입력 (InputView)**
    - 상품 선택, 수량 입력, 멤버십 할인 적용 여부 입력, 추가 구매 여부 입력 등을 처리합니다.

- [x] **입력 검증 (InputValidator)**
    - 입력된 상품과 수량의 형식을 검증하며 유효성 검사를 수행합니다.

- [x] **Service & Model**
    - `Inventory`: 재고 관리
    - `ProductLoader`: 제품 및 프로모션 정보 로드
    - `Order`: 주문 정보 저장 및 계산
    - `Product`: 제품 정보 저장 및 관리
    - `Promotion`: 프로모션 정보 관리
    - `OrderService`: 주문 생성, 제품 추가, 할인 적용 및 최종 결제 계산
    - `MembershipDiscountCalculator`: 멤버십 할인 계산
    - `PricingService`: 가격 계산
    - `ProductService`: 재고 차감 및 무료 수량 계산
    - `PromotionProcessor`: 프로모션 처리
    - `StockManager`: 재고 관리

- [x] **출력기 (OutputView)**
    - 환영 메시지, 제품 목록, 에러 메시지, 영수증 출력 등을 관리합니다.
    - `ReceiptPrinter`: 영수증 세부 항목과 최종 금액 출력
    - `ErrorMessage` :  에러 메시지
    - `ViewMessage` : 출력 메시

- [x] **Handler**
    - `OrderHandler`: 제품 선택, 멤버십 할인, 추가 구매 여부를 포함한 주문 처리 통합
    - `InputHandler`: 프로모션이 적용되지 않는 상품 구매 및 무료 프로모션 추가 여부를 사용자에게 확인합니다.

- [x] **컨트롤러 (StoreController)**
    - 상품 목록 출력, 주문 처리, 추가 구매 여부 확인 담당

- [x] **AppConfig**
    - 객체 생성 및 의존성 주입 설정
- [x] **Promotion Strategy**: 프로모션 전략 인터페이스와 구현 클래스
    - `PromotionStrategy`: 공통 프로모션 계산 인터페이스
    - `FlashSalePromotion`: 20% 할인된 수량 계산
    - `OnePlusOnePromotion`: 1+1 혜택, 구매 수량의 절반이 무료
    - `TwoPlusOnePromotion`: 2+1 혜택, 구매 수량의 1/3이 무료

- **StockValidator**: 재고 유효성 검사

- **ParsingUtils**: 문자열 파싱 유틸리티

----

## 프로그래밍 요구 사항

### 요구 사항 1

- **JDK 21 호환성**: JDK 21 버전에서 실행 가능해야 합니다.
- **프로그램 시작점**: `Application` 클래스의 `main()` 메서드를 통해 시작합니다.
- **외부 라이브러리 제한**: `build.gradle`을 수정하지 않고 제공된 라이브러리만 사용합니다.
- **프로그램 종료**: `System.exit()`를 호출하지 않습니다.
- **파일 및 패키지 이름**: 명시되지 않은 한 파일과 패키지의 이름을 변경하거나 이동하지 않습니다.
- **코드 스타일**: Java Style Guide에 맞추어 작성합니다.

### 요구 사항 2

- **인덴트 깊이 제한**: 들여쓰기 depth는 최대 2까지만 허용합니다.
- **삼항 연산자 금지**: 삼항 연산자를 사용하지 않습니다.
- **단일 기능 메서드**: 함수(또는 메서드)는 한 가지 작업만 수행하도록 작게 구현합니다.
- **테스트 코드 작성**: `JUnit 5`와 `AssertJ`를 사용하여 모든 기능 목록이 정상적으로 작동하는지 테스트합니다.

### 요구 사항 3

- **`else` 및 `switch/case` 금지**: `else`와 `switch/case` 구문을 사용하지 않습니다.
- **Java Enum 활용**: `Java Enum`을 적용하여 프로그램을 구현합니다.
- **단위 테스트 작성**: 구현된 기능에 대한 단위 테스트를 작성합니다.
    - 단, UI 로직(System.out, System.in, Scanner)은 테스트에 포함하지 않습니다.

### 요구 사항 4

- **메서드 길이 제한**: 함수(또는 메서드)의 길이는 10줄을 초과하지 않도록 구현합니다.
- **단일 책임 원칙**: 함수(또는 메서드)는 한 가지 작업만 수행하도록 작성합니다.
- **입출력 클래스 구현**: 입출력을 처리하는 클래스를 별도로 구현합니다.
    - 예시: `InputView`, `OutputView` 클래스
- **캠프 유틸 라이브러리 사용**: `camp.nextstep.edu.missionutils`의 `DateTimes`와 `Console` API를 사용하여 구현합니다.
    - **현재 날짜와 시간**: `DateTimes.now()`를 사용합니다.
    - **사용자 입력**: `Console.readLine()`을 사용하여 입력을 처리합니다.
