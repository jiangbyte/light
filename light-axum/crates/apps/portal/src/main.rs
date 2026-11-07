#[tokio::main]
async fn main() {
    let listener = tokio::net::TcpListener::bind("0.0.0.0:8080")
        .await
        .expect("bind");
    println!("应用 'apps/portal' 启动，端口 8080");
    axum::serve(listener, triggers_portal::router("portal"))
        .await
        .expect("serve");
}
