# CSCA5303 Module 1 Lab — ICMP Echo

## Tasks
- Run `ping <host>` from terminal, capture request/response in Wireshark.
- Craft ICMP Echo using a minimal Go raw-socket program and capture in Wireshark.
- Compare behaviors and note differences (TTL, checksum, sizes, ICMP id/seq, timeouts).

## Go (raw ICMP)
Requires root privileges.
```bash
sudo go run ping_raw_icmp.go
```
The target host and count are hardcoded in the file for simplicity.

## Notes on Wireshark
- Filter: `icmp` or `icmp && ip.dst == <target>`
- Observe IP TTL, ICMP type/code, identifier/sequence, checksum, payload length.

## AI Tooling Disclosure
Portions of this lab were assisted by an AI coding assistant as allowed by the course policy. Cite: CSCA 5303 Security and Ethical Hacking — Attacking the Network (OEE: Aug 25–Oct 17).
