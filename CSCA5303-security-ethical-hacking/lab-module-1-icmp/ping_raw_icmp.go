// ICMP Echo (raw socket) without external deps.
// Usage: sudo go run ping_raw_icmp.go
package main

import (
    "encoding/binary"
    "fmt"
    "net"
    "os"
    "time"
)

// checksum calculates Internet checksum for ICMP.
func checksum(b []byte) uint16 {
    var sum uint32
    l := len(b)
    for i := 0; i < l-1; i += 2 {
        sum += uint32(binary.BigEndian.Uint16(b[i:]))
    }
    if l%2 == 1 {
        sum += uint32(b[l-1]) << 8
    }
    for (sum >> 16) > 0 {
        sum = (sum & 0xFFFF) + (sum >> 16)
    }
    return ^uint16(sum)
}

func main() {
    // Hardcode target and count for lab simplicity
    dst := "yahoo.com"
    count := 4
    timeout := 2 * time.Second

    // Resolve to IPv4
    ipAddrs, err := net.LookupIP(dst)
    if err != nil || len(ipAddrs) == 0 {
        fmt.Fprintf(os.Stderr, "lookup failed: %v\n", err)
        os.Exit(1)
    }
    var ip net.IP
    for _, a := range ipAddrs {
        if v4 := a.To4(); v4 != nil {
            ip = v4
            break
        }
    }
    if ip == nil {
        fmt.Fprintln(os.Stderr, "no IPv4 address found")
        os.Exit(1)
    }

    // Raw ICMP requires root. On macOS/Linux, use "ip4:icmp".
    conn, err := net.DialIP("ip4:icmp", nil, &net.IPAddr{IP: ip})
    if err != nil {
        fmt.Fprintf(os.Stderr, "dial raw icmp failed: %v\n", err)
        os.Exit(1)
    }
    defer conn.Close()

    pid := os.Getpid() & 0xFFFF

    var received int
    for seq := 1; seq <= count; seq++ {
        // Build ICMP Echo Request: Type 8, Code 0, Checksum 0, ID, Seq
        msg := make([]byte, 8)
        msg[0] = 8 // echo request
        msg[1] = 0 // code
        // checksum placeholder at [2:4]
        binary.BigEndian.PutUint16(msg[4:], uint16(pid))
        binary.BigEndian.PutUint16(msg[6:], uint16(seq))
        csum := checksum(msg)
        binary.BigEndian.PutUint16(msg[2:], csum)

        start := time.Now()
        if _, err := conn.Write(msg); err != nil {
            fmt.Fprintf(os.Stderr, "write failed: %v\n", err)
            continue
        }

        _ = conn.SetReadDeadline(time.Now().Add(timeout))
        buf := make([]byte, 1500)
        n, addr, err := conn.ReadFrom(buf)
        rtt := time.Since(start)
        if err != nil {
            fmt.Printf("Request timeout for icmp_seq %d\n", seq)
        } else if n >= 8 && buf[0] == 0 { // Echo Reply type 0
            received++
            fmt.Printf("%d bytes from %s: icmp_seq=%d time=%.2f ms\n", n, addr.String(), seq, float64(rtt.Microseconds())/1000.0)
        } else {
            fmt.Printf("unexpected reply (len=%d)\n", n)
        }
        time.Sleep(time.Second)
    }

    loss := float64(count-received) / float64(count) * 100
    fmt.Printf("--- %s ping statistics ---\n", dst)
    fmt.Printf("%d packets transmitted, %d received, %.1f%% packet loss\n", count, received, loss)
}
