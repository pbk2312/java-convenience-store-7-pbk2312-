package store.model;

import org.junit.jupiter.api.Test;

public class MemberTest {

    @Test
    public void testMemberCreationForRegularCustomer() {
        Member member = new Member("Alice", false);
        assertThat(member.getName()).isEqualTo("Alice");
        assertThat(member.isMember()).isFalse();
    }

    @Test
    public void testMemberCreationForMemberCustomer() {
        Member member = new Member("Bob", true);
        assertThat(member.getName()).isEqualTo("Bob");
        assertThat(member.isMember()).isTrue();
    }

}
