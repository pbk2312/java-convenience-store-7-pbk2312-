# java-convenience-store-precourse

---

# 기능 목록 정리

- [x] ## 입력(InputView)
- `inputProductSelection()`:
    - 사용자로부터 상품 선택 및 수량을 입력받습니다.
- `inputMembershipChoice()`:
    - 사용자에게 멤버십 할인 적용 여부를 묻고 입력을 받습니다.
- `inputAdditionalPurchase()`:
    - 추가 구매 여부를 묻고 입력을 받습니다.
- `inputPromotionAdd(String productName, int quantity)`:
    - 프로모션 적용이 가능한 상품에 대해 고객이 해당 수량만큼 가져오지 않았을 경우,혜택에 대한 안내
- `inputPromotionLack(String productName, int quantity)`:
    - 프로모션 재고가 부족하여 일부 수량을 프로모션 혜택 없이 결제해야 하는 경우,일부 수량에 대해 정가로 결제할지 여부에 대한 안내
- `close()`:
    - 콘솔 자원을 해제합니다.
- `readInput(String message)`:
    - 주어진 메시지를 콘솔에 출력하고 사용자 입력을 읽어 반환하는 내부 유틸리티 메소드
    - `Console.readLine()`을 통해 입력을 받아옵니다.

- [x] ## 입력에 대한 검증 (InputValidator)

- `validateProductSelection(String input, Map<String, Integer> productInventory)`:
    - 사용자가 입력한 상품과 수량의 형식을 검증하고 입력이 유효한지 확인합니다.
    - 예외 발생 예시:
        - 빈 입력: `''` → `[ERROR] 입력이 비어 있습니다. 값을 입력해 주세요.` (`EMPTY_INPUT`)
        - 형식 오류 (상품명이 빈 경우): `'[-10]'` → `[ERROR] 올바른 형식으로 입력해 주세요. 예: [콜라-10],[사이다-3]` (`INVALID_FORMAT`)
        - 존재하지 않는 상품: `'[없는상품-3]'` → `[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.` (`NON_EXISTENT_PRODUCT`)

- `validateProductDetails(String product, Map<String, Integer> productInventory)`:
    - 개별 상품의 이름과 수량이 유효한지 확인합니다.

- `validateFormat(String product)`:
    - 상품과 수량이 "[상품명-수량]" 형식에 맞는지 검증합니다.
    - 형식이 유효하지 않을 경우 `INVALID_FORMAT` 오류 메시지를 출력합니다.
    - 예외 발생 예시:
        - 잘못된 구분자 사용: `'[콜라=10]'` → `[ERROR] 올바른 형식으로 입력해 주세요. 예: [콜라-10],[사이다-3]` (`INVALID_FORMAT`)
        - 쉼표 없는 구분: `'[콜라-10][사이다-3]'` → `[ERROR] 올바른 형식으로 입력해 주세요. 예: [콜라-10],[사이다-3]` (`INVALID_FORMAT`)
        - 하이픈 없는 경우: `'[콜라10],[사이다-3]'` → `[ERROR] 올바른 형식으로 입력해 주세요. 예: [콜라-10],[사이다-3]` (`INVALID_FORMAT`)

- `validateNonZeroQuantity(int quantity)`:
    - 입력된 수량이 0이 아닌지 검증합니다.
    - 수량이 0일 경우 `ZERO_QUANTITY` 오류 메시지를 출력합니다.
    - 예외 발생 예시:
        - 수량이 0인 경우: `'[콜라-0]'` → `[ERROR] 수량은 0보다 커야 합니다.` (`ZERO_QUANTITY`)

- `validateProductExistsInInventory(String productName, Map<String, Integer> productInventory)`:
    - 입력한 상품이 상품 목록에 존재하는지 확인합니다.
    - 상품이 존재하지 않을 경우 `NON_EXISTENT_PRODUCT` 오류 메시지를 출력합니다.
    - 예외 발생 예시:
        - 존재하지 않는 상품: `'[없는상품-3]'` → `[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.` (`NON_EXISTENT_PRODUCT`)

- `validateSufficientStock(String productName, int quantity, Map<String, Integer> productInventory)`:
    - 입력한 수량이 재고를 초과하지 않는지 확인합니다.
    - 수량이 재고보다 많을 경우 `INVALID_QUANTITY` 오류 메시지를 출력합니다.
    - 예외 발생 예시:
        - 재고 초과 수량 입력: `'[콜라-15]'` (재고 10) → `[ERROR] 요청한 수량이 유효하지 않거나 재고를 초과합니다.` (`INVALID_QUANTITY`)
        - 재고가 없는 상품 선택: `'[우아한돼지들-1]'` (재고 0) → `[ERROR] 요청한 수량이 유효하지 않거나 재고를 초과합니다.` (`INVALID_QUANTITY`)

- `validateNotEmpty(String input)`:
    - 입력이 비어 있지 않은지 확인합니다.
    - 비어 있거나 공백인 경우 `EMPTY_INPUT` 오류 메시지를 출력합니다.
    - 예외 발생 예시:
        - 빈 입력: `''` → `[ERROR] 입력이 비어 있습니다. 값을 입력해 주세요.` (`EMPTY_INPUT`)

- `validateYesOrNo(String input)`:
    - 입력이 "Y" 또는 "N"인지 확인합니다.
    - 이외의 값을 입력할 경우 `INVALID_YES_NO` 오류 메시지를 출력합니다.
    - 예외 발생 예시:
        - 잘못된 값: `'우아한테크코스'` → `[ERROR] Y 또는 N을 입력해 주세요.` (`INVALID_YES_NO`)
        - 소문자 입력: `'y'` 또는 `'n'` → `[ERROR] Y 또는 N을 입력해 주세요.` (`INVALID_YES_NO`)

## 재고 관리 기능

- 각 상품의 재고 수량을 고려하여 결제 가능 여부를 확인 (Service)
- 구매된 상품 수량만큼 재고에서 차감하여 최신 재고 상태를 유지 (Service)
- 재고가 부족할 경우 사용자에게 알림과 함께 결제 진행을 차단할지 여부는 추가 검토 (Service)

## 프로모션 할인 기능

- 오늘 날짜가 프로모션 기간에 포함되면 프로모션 적용 (Service)
- N개 구매 시 1개 무료 증정(Buy N Get 1 Free) 형태로 프로모션 진행 (Service)
- 특정 상품에만 1+1 또는 2+1 프로모션이 적용되며 동일 상품에 여러 프로모션은 적용되지 않음 (Service)
- 프로모션 재고를 우선 차감하고 부족 시 일반 재고 사용 (Service)
- 프로모션 수량 부족 시 혜택 적용 가능 수량 안내 (Service)
- 프로모션 재고가 부족해 일부 수량이 정가로 결제될 경우 이를 안내 (Service)

## 멤버십 할인 기능

- 멤버십 회원은 프로모션이 적용되지 않은 금액의 30% 할인 (Service)
- 프로모션 적용 후 남은 금액에 대해 멤버십 할인 적용 (Service)
- 멤버십 할인 최대 한도는 8000원 (Service)

## 영수증 출력 기능

- 고객의 구매 내역과 할인 정보 요약하여 영수증 출력 (View)
    - **구매 상품 내역**: 상품명, 수량, 금액
    - **증정 상품 내역**: 프로모션으로 무료 제공된 상품 목록
    - **총 구매액**: 모든 상품의 총 금액
    - **행사 할인**: 프로모션으로 할인된 금액
    - **멤버십 할인**: 멤버십으로 추가 할인된 금액
    - **내실 돈**: 최종 결제 금액
- 영수증 구성 요소를 정렬하여 보기 쉽게 출력 (View)

## 추가 구매 및 종료 기능

- 영수증 출력 후 추가 구매 진행 여부를 사용자에게 묻고 선택에 따라 다음 단계로 진행 (Controller)
