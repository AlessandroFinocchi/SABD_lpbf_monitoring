export bench_id=$(curl -X POST http://localhost:8866/api/create -k \
     -H "Content-Type: application/json" \
     -d '{"apitoken": "abcdefgd", "test": true}')
#
export bench_id=$(curl -X POST http://micro-challenger:8866/api/create -k \
    -H "Content-Type: application/json" \
    -d '{"apitoken": "abcdefgd", "test": true}')

echo $bench_id