# java-convenience-store-precourse

---

# 기능 요구 사항

### 결제 시스템

- **최종 결제 금액 계산**: 구매자가 입력한 상품 가격과 수량을 기반으로 최종 결제 금액을 계산합니다.
    - 상품별 가격과 수량을 곱한 총구매액을 계산하고, 프로모션 및 멤버십 할인 정책을 반영하여 최종 결제 금액을 산출합니다.
    - 구매 내역과 산출한 금액 정보를 영수증으로 출력합니다.
- **추가 구매 여부**: 영수증 출력 후 추가 구매를 진행할지 종료할지 선택할 수 있습니다.
- **입력 오류 처리**: 잘못된 값이 입력될 경우 `IllegalArgumentException`을 발생시키고, "[ERROR]"로 시작하는 에러 메시지를 출력한 후 입력을 다시 받습니다.
    - 명확한 예외 유형인 `IllegalArgumentException`과 `IllegalStateException` 등을 처리합니다.

### 재고 관리

- **재고 수량 확인**: 각 상품의 재고 수량을 고려하여 결제가 가능한지 확인합니다.
- **재고 차감**: 고객이 상품을 구매할 때마다 해당 수량만큼 재고에서 차감하여 최신 재고 상태를 유지합니다.
- **재고 정보 제공**: 재고를 최신 상태로 유지하여 다음 고객이 구매할 때 정확한 재고 정보를 제공합니다.

### 프로모션 할인

- **프로모션 기간 확인**: 오늘 날짜가 프로모션 기간 내에 포함된 경우에만 할인을 적용합니다.
- **프로모션 형태**: `N+1` 형태의 할인으로, 지정된 상품에 대해 `1+1` 또는 `2+1` 프로모션이 적용됩니다.
    - 동일 상품에 여러 프로모션이 중복 적용되지 않습니다.
    - 프로모션은 프로모션 재고 내에서만 적용되며, 프로모션 재고가 부족할 경우 일반 재고를 사용합니다.
- **프로모션 혜택 안내**:
    - 고객이 프로모션 혜택을 받을 수 있는 수량보다 적게 가져온 경우, 필요한 수량을 추가로 가져오면 혜택을 받을 수 있음을 안내합니다.
    - 프로모션 재고가 부족하여 일부 수량을 정가로 결제해야 할 때 안내 메시지를 제공합니다.

### 멤버십 할인

- **할인율**: 멤버십 회원은 프로모션 미적용 금액의 30%를 할인받습니다.
- **적용 방식**: 프로모션 적용 후 남은 금액에 대해 멤버십 할인을 적용하며, 최대 할인 한도는 8,000원입니다.

### 영수증 출력

- **구매 내역**: 고객이 구매한 상품의 이름, 수량, 가격을 포함합니다.
- **증정 상품 내역**: 프로모션에 따라 무료로 제공된 상품 목록을 포함합니다.
- **금액 정보**:
    - **총구매액**: 구매한 상품의 총 수량과 총 금액입니다.
    - **행사할인**: 프로모션으로 인해 할인된 금액입니다.
    - **멤버십할인**: 멤버십에 의해 추가로 할인된 금액입니다.
    - **내실돈**: 최종 결제 금액입니다.
- **영수증 레이아웃**: 구성 요소를 보기 좋게 정렬하여 고객이 쉽게 금액과 수량을 확인할 수 있도록 합니다.

----

# 기능 목록 정리

[x]  입력(InputView)

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

[x]  입력에 대한 검증 (InputValidator)

- `validateProductSelectionFormat(String input)`:
    - 사용자가 입력한 상품과 수량의 형식을 검증하고,입력이 유효한지 확인합니다.

- `validateFormat(String product)`:
    - 상품과 수량이 "[상품명-수량]" 형식에 맞는지 검증합니다.
    - 형식이 유효하지 않을 경우 `INVALID_FORMAT` 오류 메시지를 출력합니다.
    - 예외 발생 예시:
        - 잘못된 구분자 사용: `'[콜라=10]'` → `[ERROR] 올바른 형식으로 입력해 주세요. 예: [콜라-10],[사이다-3]` (`INVALID_FORMAT`)
        - 하이픈 없는 경우: `'[콜라10]'` → `[ERROR] 올바른 형식으로 입력해 주세요. 예: [콜라-10]` (`INVALID_FORMAT`)

- `validateNotEmpty(String input)`:
    - 입력값이 `null`이거나 공백인지 검증합니다.
    - 빈 입력일 경우 `EMPTY_INPUT` 오류 메시지를 출력합니다.
    - 예외 발생 예시:
        - 빈 입력: `''` → `[ERROR] 입력이 비어 있습니다. 값을 입력해 주세요.` (`EMPTY_INPUT`)

- `validateYesOrNo(String input)`:
    - 입력값이 "Y" 또는 "N"인지 검증합니다.
    - 유효하지 않은 입력일 경우 `INVALID_YES_NO` 오류 메시지를 출력합니다.
    - 예외 발생 예시:
        - `'A'` → `[ERROR] Y 또는 N을 입력해 주세요.` (`INVALID_YES_NO`)
        - `'Yes'` → `[ERROR] Y 또는 N을 입력해 주세요.` (`INVALID_YES_NO`)

[x] Service & model

## Inventory (재고 관리)

`Inventory` 클래스는 상점의 재고 관리를 담당

- **`addProduct(Product product)`**: `Product` 객체를 `productList`에 추가합니다.
- **`getProductList()`**: 현재 재고 목록을 반환합니다.
- **`adjustProductStock(Product product)`**: 제품 이름과 프로모션 상태가 일치하는 제품의 재고를 업데이트합니다.
- **`getPromotionProductByName(String productName)`**: 특정 이름의 프로모션 제품을 반환합니다.
- **`getRegularProductByName(String productName)`**: 특정 이름의 일반 제품을 반환합니다.
- **`clear()`**: `productList`를 초기화합니다.

## 제품 및 프로모션 로더 (ProductLoader)

`ProductLoader` 클래스는 `products.md`,`promotions.md` 파일에서 제품과 프로모션 정보를 로드하여 `Inventory`에 저장합니다.

- **`loadProducts(Inventory inventory, Map<String, Promotion> promotions)`**:
    - 제품 파일을 읽어 `Inventory`에 제품을 추가합니당.
    - 중복된 제품 개수를 확인하여 필요한 경우 재고가 0인 제품을 추가!!

- **`countProductOccurrences()`**:
    - 각 제품이 몇 번 등장하는지 계산하고 `Map<String, Integer>`로 반환합니다.
    - 파일 읽기 오류 시 `PRODUCT_LOAD_ERROR` 메시지를 출력합니다.

- **`addProductsToInventory(Inventory inventory, Map<String, Promotion> promotions, Map<String, Integer> productCount)`
  **:
    - 파일의 각 줄을 처리하여 `Product` 객체를 생성한 후 `Inventory`에 추가하며,필요 시 재고가 0인 제품을 추가합니다.

- **`addZeroStockProductIfNeeded(Inventory inventory, Product product, Map<String, Integer> productCount)`**:
    - 특정 프로모션이 적용되고 한 번만 나타나는 경우,재고가 0인 제품을 `Inventory`에 추가합니다.

- **`parseProduct(String line, Map<String, Promotion> promotions)`**:
    - 제품 파일의 한 줄을 파싱하여 `Product` 객체를 생성하고 반환합니다.

- **`getPromotionIfExists(String promotionName, Map<String, Promotion> promotions)`**:
    - 프로모션 이름이 존재하면 해당 프로모션 객체를 반환하고, 그렇지 않으면 `null`을 반환합니다.

- **`loadPromotions()`**:
    - 프로모션 파일을 읽어 `Map<String, Promotion>` 형태로 로드합니다.
    - 오류 발생 시 `PROMOTION_LOAD_ERROR` 메시지를 출력합니다.

- **`addPromotionFromLine(String line, Map<String, Promotion> promotions)`**:
    - 프로모션 파일의 각 줄을 읽어 `Promotion` 객체를 생성하여 `Map` 에 추가합니다.

### 예외 처리 및 메시지

- **`logError(ErrorMessage errorMessage, Exception e)`**:
    - 오류 메시지를 출력하고 `IllegalArgumentException`을 던집니다.
        - `PRODUCT_LOAD_ERROR`와 `PROMOTION_LOAD_ERROR` 메시지를 사용해 오류를 처리합니다.

## Order (주문)

      `Order` 클래스는 주문에 대한 정보를 저장하고 계산하는 역할을 합니다.

- **필드**:
    - `orderedProducts`: 주문된 제품과 수량을 저장
    - `freeItems`: 무료로 제공된 제품과 수량을 저장
    - `isMembership`: 멤버십 여부
    - `totalQuantity`: 총 주문 수량
    - `totalBeforeDiscount`: 할인 전 총 금액
    - `eventDiscount`: 이벤트 할인 금액
    - `membershipDiscount`: 멤버십 할인 금액
    - `finalTotal`: 최종 결제 금액

- **메서드**:
    - **`addProduct(Product product, int quantity)`**: 주문에 제품과 수량을 추가하고,총 금액과 수량을 업데이트합니다.
    - **`setEventDiscount(double discount)`**: 이벤트 할인을 설정합니다.
    - **`setMembershipDiscount(double discount)`**: 멤버십 할인을 설정합니다.
    - **`setFinalTotal(double total)`**: 최종 결제 금액을 설정합니다.
    - **`getOrderedProducts()`**: 주문된 제품 목록을 반환합니다.
    - **`getFreeItems()`**: 무료로 제공된 제품 목록을 반환합니다.
    - **`getTotalQuantity()`**: 총 주문 수량을 반환합니다.
    - **`getTotalBeforeDiscount()`**: 할인 전 총 금액을 반환합니다.
    - **`getEventDiscount()`**: 이벤트 할인 금액을 반환합니다..
    - **`getMembershipDiscount()`**: 멤버십 할인 금액을 반환합니다,
    - **`getFinalTotal()`**: 최종 결제 금액을 반환합니다.
    - **`isMembership()`**: 멤버십 여부를 반환합니다.
    - **`setMembership(boolean isMembership)`**: 멤버십 여부를 설정합니다.
    - **`getAggregatedOrderSummary()`**: 제품별 요약 정보를 반환합니다.

    - **`updateSummaryMap(Map<String, ProductSummary> summaryMap, Product product, int quantity)`**: 요약 정보에 제품별 수량과 금액을
      추가합니다.
    - **`calculateTotalProductPrice(Product product, int quantity)`**: 제품별 총 가격을 계산하여 반환합니다.

## Product (제품)

`Product` 클래스는 제품의 정보를 저장하고 재고 관리 및 프로모션 적용 여부를 확인하는 기능을 제공합니다.

- **필드**:
    - `name`: 제품 이름
    - `price`: 제품 가격
    - `stock`: 현재 재고 수량
    - `promotion`: 제품에 적용된 프로모션 객체


- **메서드**:
    - **`getName()`**: 제품명을 반환합니다.
    - **`getPrice()`**: 제품 가격을 반환합니다.
    - **`getStock()`**: 현재 재고를 반환합니다.
    - **`getPromotion()`**: 제품에 적용된 프로모션을 반환합니다.
    - **`adjustStock(int quantity)`**: 재고 수량을 차감합니다.
    - **`hasPromotion()`**: 현재 활성화된 프로모션이 있는지 여부를 반환합니다.

## Promotion (프로모션)

`Promotion` 클래스는 제품에 적용되는 프로모션 정보를 저장하고, 프로모션 기간과 할인 전략을 관리합니다.

- **필드**:
    - `strategy`: 프로모션 전략 (`PromotionStrategy`)
    - `startDate`: 프로모션 시작일
    - `endDate`: 프로모션 종료일
    - `description`: 프로모션 설명


- **메서드**:
    - **`isActive(LocalDate currentDate)`**: 현재 날짜가 프로모션 기간에 해당하는지 여부를 반환합니다.
    - **`calculateDiscountedPrice(int quantity, double price)`**: 프로모션이 활성화된 경우,할인된 가격을 계산하여 반환합니다.
    - **`getFreeQuantity(int quantity)`**: 프로모션이 활성화된 경우,무료 제공 수량을 반환합니다.
    - **`getDescription()`**: 프로모션 설명을 반환합니다.
    - **`getStrategy()`**: 프로모션 전략을 반환합니다.

## OrderService (주문 서비스)

`OrderService` 클래스는 주문 생성, 제품 추가, 할인 적용 및 최종 결제 금액 계산을 담당합니다.

- **필드**:
    - `stockManager`: 재고 관리 객체
    - `promotionProcessor`: 프로모션 처리 객체
    - `stockValidator`: 재고 검증 객체
    - `pricingService`: 가격 계산 서비스
    - `membershipDiscountCalculator`: 멤버십 할인 계산기
    - `inputHandler`: 사용자 입력 처리 객체

- **메서드**:
    - **`createOrder()`**: 새로운 `Order` 객체를 생성하여 반환합니다.
    - **`addProductToOrder(Order order, String productName, int quantity)`**: 주문에 제품을 추가하고 프로모션과 재고를 검증하며 필요한 경우 무료 항목을
      처리합니다.
    - **`validateAndDeductStock(Order order, Product promotionProduct, Product regularProduct, int quantity)`**: 재고를
      검증하고, 재고 차감 및 주문 추가를 수행합니다.
    - **`handleFreeItems(Order order, Product promotionProduct, int quantity)`**: `OnePlusOnePromotion`이 적용된 제품에 대해 무료
      항목 추가 여부를 확인합니다.
    - **`applyMembershipDiscount(Order order, boolean isMembership)`**: 멤버십 상태를 주문에 설정합니다.
    - **`calculateFinalTotal(Order order)`**: 이벤트 할인과 멤버십 할인을 적용하여 최종 결제 금액을 계산합니다.
    - **`calculateEventDiscount(Order order)`**: 주문 항목별 이벤트 할인을 계산하여 총 할인 금액을 반환합니다.
    - **`calculateProductDiscount(Product product, int quantity)`**: 개별 제품에 대한 할인을 계산하여 반환합니다.
    - **`finalizeTotal(Order order, double eventDiscount, double membershipDiscount)`**: 최종 결제 금액을 계산하고 `Order` 객체에
      설정합니다.

## MembershipDiscountCalculator (멤버십 할인 계산기)

- **필드**:
    - `MEMBERSHIP_DISCOUNT_RATE`: 멤버십 할인율 (30%)
    - `MAX_MEMBERSHIP_DISCOUNT`: 최대 멤버십 할인 금액 (8000원)

- **메서드**:
    - **`calculate(double amountAfterEventDiscount, boolean isMembership)`**: 멤버십 할인 금액을 계산하여 반환하며 최대 할인 금액을 초과하지 않습니다.

## PricingService (가격 계산 서비스)

- **메서드**:
    - **`calculateFinalPrice(Product product, int quantity)`**: 프로모션이 적용된 제품의 경우, 무료 수량을 제외한 금액을 계산하여 반환합니다. 프로모션이 없으면
      정가로 계산합니다.

## ProductService (제품 서비스)

- **메서드**:
    - **`deductStock(Product product, int quantity)`**: 요청한 수량이 재고보다 많으면 예외를 발생시키고, 그렇지 않으면 재고에서 수량을 차감합니다.
    - **`calculateFreeItems(Product product, int quantity)`**: 프로모션이 활성화된 경우 무료로 제공할 수량을 계산하여 반환합니다.

## PromotionProcessor (프로모션 처리기)

- **필드**:
    - `stockManager`: 재고 관리 객체
    - `promotionService`: 프로모션 서비스
    - `inputHandler`: 사용자 입력 처리 객체

- **메서드**:
    - **`applyPartialPromotion(Order order, Product promotionProduct, Product regularProduct, int quantity)`**: 프로모션 재고와
      일반 재고를 나누어 주문에 추가합니다
    - **`applyPromotionalStock(Order order, Product promotionProduct, int usedPromotionQuantity)`**: 프로모션 재고에서 차감하여 주문에
      추가하고, 무료 항목을 제공합니다
    -
        *
  *`applyNonPromotionalStock(Order order, Product regularProduct, int remainingQuantity, int requestedQuantity, int fullPromotionQuantity)`
  **: 남은 수량에 대해 정가 구매를 확인하고, 주문에 추가합니다.
    - **`confirmPurchaseWithoutPromotion(Product product, int remainingQuantity)`**: 프로모션이 적용되지 않은 수량에 대한 구매 여부를 사용자에게
      확인합니다.

## StockManager (재고 관리자)

- **필드**:
    - `inventory`: 재고 관리 객체 (`Inventory`)
    - `productService`: 제품 서비스 (`ProductService`)

- **메서드**:
    - **`getPromotionProduct(String productName)`**: 프로모션이 적용된 제품을 `Optional`로 반환합니다.
    - **`getRegularProduct(String productName)`**: 프로모션이 없는 일반 제품을 반환하며 존재하지 않으면 예외를 발생시킵니다.
    - **`deductStock(Product product, int quantity)`**: 제품 서비스에서 재고를 차감하며 재고 정보를 업데이트합니다.
      [x]  출력기 (OutputView)

## OutputView (출력 뷰)

- **필드**:
    - `receiptPrinter`: 영수증 출력

- **메서드**:
    - **`displayWelcomeMessage()`**: 환영 메시지를 출력합니다.
    - **`displayProductList(Inventory inventory)`**: 제품 목록을 출력합니다.
    - **`displayProduct(Product product)`**: 단일 제품 정보를 출력합니다.
    - **`printErrorMessage(String errorMessage)`**: 에러 메시지를 출력합니다.
    - **`printReceipt(Order order)`**: 주문 영수증을 출력합니다.

- **Helper Methods**:
    - **`formatPrice(double price)`**: 가격을 한국 통화 형식으로 변환합니다.
    - **`getPromotionText(Product product)`**: 제품의 프로모션 정보를 반환합니다.
    - **`getStockText(Product product)`**: 제품의 재고 상태를 반환합니다.

[x] # Hanlder

## OrderHandler (주문 통합 처리)

- **필드**:
    - `inputView`: 사용자 입력 뷰
    - `orderService`: 주문 서비스
    - `outputView`: 출력 뷰

- **메서드**:
    - **`processProductSelection(Order order)`**: 제품 선택을 처리하고 주문에 추가합니다.
    - **`processMembershipChoice(Order order)`**: 멤버십 할인을 선택하고 적용합니다.
    - **`confirmAdditionalPurchase()`**: 추가 구매 여부를 확인하고 반환합니다.
    - **`addProductsToOrder(Order order, String input)`**: 입력된 제품 목록을 주문에 추가합니다.
    - **`attemptToAddSingleProduct(Order order, String productInfo)`**: 단일 제품을 주문에 추가하고 오류를 처리합니다.
    - **`processWithValidation(Supplier<String> inputSupplier, Consumer<String> validationFunction)`**: 입력 검증을 수행하며 작업을
      처리합니다.
    - **`processWithValidationAndReturn(Supplier<String> inputSupplier, Function<String, T> validationFunction)`**: 입력
      검증 후 결과를 반환합니다.

[x] # 컨트롤러(storeController)

## StoreController (스토어 컨트롤러)

- **필드**:
    - `inventory`: 재고 정보 (`Inventory`)
    - `orderHandler`: 주문 처리 객체 (`OrderHandler`)
    - `orderService`: 주문 서비스 (`OrderService`)
    - `outputView`: 출력 뷰 (`OutputView`)

- **메서드**:
    - **`start()`**: 상품 목록을 출력하고 첫 주문을 처리하며,추가 구매 여부에 따라 반복 처리합니다.
    - **`processOrder(Order order)`**: 주문에 제품을 추가하고 멤버십 할인 적용 여부를 확인한 후 최종 결제 금액을 계산하여 영수증을 출력합니다.

----

## 프로그래밍 요구 사항

### 요구 사항 1

- **JDK 21 호환성**: 프로그램은 JDK 21 버전에서 실행 가능해야 합니다.
- **프로그램 시작점**: `Application` 클래스의 `main()` 메서드를 통해 시작해야 합니다.
- **외부 라이브러리 제한**: `build.gradle` 파일을 수정하지 않으며, 제공된 라이브러리 이외의 외부 라이브러리를 사용하지 않습니다.
- **프로그램 종료**: `System.exit()` 호출을 통해 프로그램을 종료하지 않습니다.
- **파일 및 패키지 이름**: 명시되지 않은 한 파일이나 패키지의 이름을 바꾸거나 이동하지 않습니다.
- **코드 스타일**: Java Style Guide를 따르며, 자바 코드 컨벤션을 지켜 작성합니다.

### 요구 사항 2

- **인덴트 제한**: 인덴트 깊이(들여쓰기 depth)는 최대 2까지만 허용됩니다.
    - 예: `while`문 안에 `if`문이 있는 경우, 인덴트 깊이는 2입니다.
    - **힌트**: 함수(또는 메서드)로 기능을 분리하여 인덴트 깊이를 줄일 수 있습니다.
- **삼항 연산자 금지**: 삼항 연산자를 사용하지 않습니다.
- **단일 기능 메서드**: 함수(또는 메서드)는 한 가지 작업만 수행하도록 최대한 작게 구현합니다.
- **테스트 코드 작성**: `JUnit 5`와 `AssertJ`를 사용하여 모든 기능 목록이 정상적으로 작동하는지 테스트합니다.

### 요구 사항 3

- **`else` 및 `switch/case` 금지**: `else`와 `switch/case` 구문을 사용하지 않습니다.
    - **힌트**: `if` 조건에서 `return`을 활용하여 `else` 없이 구현할 수 있습니다.
- **Java Enum 활용**: `Java Enum`을 적용하여 프로그램을 구현합니다.
- **단위 테스트 작성**: 구현된 기능에 대한 단위 테스트를 작성합니다.
    - 단, UI 로직(System.out, System.in, Scanner)은 테스트에 포함하지 않습니다.

### 요구 사항 4

- **메서드 길이 제한**: 함수(또는 메서드)의 길이는 10줄을 초과하지 않도록 구현합니다.
- **단일 책임 원칙**: 함수(또는 메서드)는 한 가지 작업만 잘 수행하도록 작성합니다.
- **입출력 클래스 구현**: 입출력을 처리하는 클래스를 별도로 구현합니다.
    - 예시: `InputView`, `OutputView` 클래스
- **라이브러리 사용**: `camp.nextstep.edu.missionutils`의 `DateTimes`와 `Console` API를 사용하여 구현합니다.
    - **현재 날짜와 시간**: `DateTimes.now()`를 사용합니다.
    - **사용자 입력**: `Console.readLine()`을 사용하여 입력을 처리합니다.
